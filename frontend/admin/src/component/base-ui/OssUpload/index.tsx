import React, { useEffect, useRef, useState } from 'react';
import { Button, Image, message, Upload, UploadFile, UploadProps } from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import OSS from 'ali-oss';
import http from '@/utils/http';
import { has, isNumber, pick } from 'lodash';
import dayjs from 'dayjs'; // 请确保路径正确
import uuid from 'uuid';

const getPathname = (url = '') => {
	if (url && !url.startsWith('http')) {
		return url.split('?')[0].split('#')[0]; // 移除查询参数和哈希部分
	}
	// 移除协议部分（http:// 或 https://）并获取路径部分
	const match = url.match(/^https?:\/\/[^/]+(\/.*)$/);
	return match ? match[1] : '';
};

// 编码函数：将字符串进行自定义 Base64 编码
const fileNameEncoded = (input: string): string => {
	// 去掉填充字符 '='
	return uuid();
};

const OssStore: {
	credentials?: Record<string, string>;
} = {
	credentials: undefined,
};

const generateFileUrl = (filePath: string): string => {
	const domain = 'https://yoohoo-oss.oss-cn-shanghai.aliyuncs.com'; // 这里是你的 OSS 域名
	// eslint-disable-next-line no-constant-condition
	return `${domain}${filePath.startsWith('/') ? '' : '/'}${filePath}`;
};

function isTokenExpired(expiration: number) {
	if (!expiration) {
		return true;
	}

	const expirationTime = new Date(expiration * 1000); // expiration 是 Unix 时间戳，单位为秒
	const currentTime = new Date();
	return currentTime >= expirationTime;
}

const generateDatePath = () => {
	const now = new Date();
	const year = now.getFullYear();
	const month = String(now.getMonth() + 1).padStart(2, '0'); // 补齐两位
	const day = String(now.getDate()).padStart(2, '0'); // 补齐两位

	return `${year}${month}${day}`; // 返回 YYYY/MM/DD 格式
};

// 生成文件名函数
const generateFileName = (filename: string): string => {
	const timeStamp = dayjs().format('HHmmssSSS'); // 当前时间戳（小时、分钟、秒、毫秒）
	const uid = Math.floor(Math.random() * 10000); // 生成随机数

	const lastDotIndex = filename.lastIndexOf('.');
	const fileExtension = lastDotIndex !== -1 ? filename.substring(lastDotIndex + 1) : '';
	const baseName = lastDotIndex !== -1 ? filename.substring(0, lastDotIndex) : filename;

	const fullString = `${baseName}${timeStamp}${uid}`; // 拼接字符串
	const encodedBaseName = fileNameEncoded(fullString); // 编码

	return fileExtension ? `${encodedBaseName}.${fileExtension}` : encodedBaseName;
};

const getBase64 = (file: any): Promise<string> =>
	new Promise((resolve, reject) => {
		const reader = new FileReader();
		reader.readAsDataURL(file);
		reader.onload = () => resolve(reader.result as string);
		reader.onerror = error => reject(error);
	});

type FileType = {
	url: string;
	uid?: string;
	name: string;
};
export type OssUploadProps = UploadProps & {
	readonly?: boolean;
	value?: FileType[]; // 成功上传的文件列表
	onChange?: (fileList: any[]) => void; // 更新成功文件列表的回调
};

const OssUpload: React.FC<OssUploadProps> = props => {
	const { readonly, value, maxCount, onChange, ...rest } = props;

	const clientRef = useRef<any>();
	const [fileList, setFileList] = useState<any[]>([]); // 本地管理文件列表（包含成功和失败）
	const [previewOpen, setPreviewOpen] = useState(false);
	const [previewImage, setPreviewImage] = useState('');
	// 刷新凭证
	const refreshCredentials = async () => {
		const res = await http.get('/admin/oss/token');
		if (res?.code) {
			message.error('获取凭证失败');
			return;
		}
		OssStore.credentials = res.data;
		const credentials: any = getCredentials();
		const config = {
			region: 'oss-' + credentials?.region, // 阿里云 OSS 的 region，修改为你的区域
			endpoint: 'https://oss-cn-shanghai.aliyuncs.com',

			accessKeyId: credentials.accessKeyId,
			accessKeySecret: credentials.accessKeySecret,
			stsToken: credentials.securityToken,

			bucket: credentials.bucketName,
		};

		// 初始化 OSS 客户端
		clientRef.current = new OSS(config);
		return clientRef.current;
	};

	// 上传文件处理
	const customRequest = async options => {
		const { file, onProgress, onSuccess, onError } = options;
		let credentials = getCredentials();

		// 每次上传前检查凭证是否过期
		if (isTokenExpired(credentials?.expiration)) {
			await refreshCredentials(); // 刷新凭证
		}
		credentials = getCredentials();
		const client = clientRef.current;

		if (!client || !credentials) {
			message.error('OSS没有初始化好');
			return onError(new Error('OSS没有初始化好'));
		}

		const datePath = generateDatePath();
		const fileName = `dev/${datePath}/${generateFileName(file.name)}`;

		try {
			// 使用multipartUpload进行分片上传
			await client.multipartUpload(fileName, file, {
				partSize: 10 * 1024 * 1024, // 每个分片最大10MB
				progress: p => {
					// 上传进度，更新进度条
					onProgress({ percent: p * 100 }, file);

					// 更新上传进度
					setFileList(prevList =>
						prevList.map(item => (item.uid === file.uid ? { ...item, percent: p * 100 } : item))
					);
				},
				onError: (err: any) => {
					// 上传失败，更新文件状态为失败
					onError(err);
					setFileList(prevList =>
						prevList.map(item => (item.uid === file.uid ? { ...item, status: 'error' } : item))
					);
				},
			});

			// 生成签名 URL
			const url = generateFileUrl(fileName);
			// 使用 URL 对象移除查询部分
			const parsedUrl = new URL(url);
			parsedUrl.search = ''; // 清除 query 部分

			// 上传成功，更新文件状态为成功
			onSuccess({ url: parsedUrl.toString(), filename: fileName }, file);
		} catch (error) {
			// 处理错误
			onError(error);
			setFileList(prevList =>
				prevList.map(item => (item.uid === file.uid ? { ...item, status: 'error' } : item))
			);
		}
	};

	// 获取凭证
	const getCredentials = () => {
		return OssStore.credentials;
	};

	const getFormatValue = () => {
		if (value) {
			return Array.isArray(value) ? value : [value];
		}
		return [];
	};
	const renderChild = () => {
		if (readonly) {
			return null;
		}
		if (isNumber(maxCount) && getFormatValue().length >= maxCount) {
			return null;
		}
		if (props?.listType === 'picture-card') {
			return '+ 上传';
		}

		return <Button icon={<UploadOutlined />}>上传</Button>;
	};

	useEffect(() => {
		const _val = value || [];

		setFileList(old => {
			// 创建一个新的文件列表，合并 old 和 value，按照 value 的顺序处理
			const updatedFileList = getFormatValue().map(newFile => {
				// 查找 old 中是否已经有这个文件
				const matchingOldFile = old.find(item => {
					return item.uid === newFile.uid || getPathname(item.url) === getPathname(newFile.url);
				});

				// 如果找到了匹配的文件，使用 old 中的文件
				if (matchingOldFile) {
					return matchingOldFile;
				}

				// 否则，使用 value 中的文件
				return {
					...newFile,
					url: generateFileUrl(getPathname(newFile.url)),
				};
			});

			// 返回合并后的文件列表
			return updatedFileList;
		});
	}, [value]); // 当 value 改变时，执行这个 effect

	const handlePreview = async (file: UploadFile) => {
		if (!file.url && !file.preview) {
			file.preview = await getBase64(file.originFileObj as any);
		}

		setPreviewImage(file.url || (file.preview as string));
		setPreviewOpen(true);
	};

	return (
		<>
			<Upload
				customRequest={customRequest}
				showUploadList={{
					showDownloadIcon: !readonly, // 显示预览图标
					showRemoveIcon: !readonly, // 显示删除图标
				}}
				maxCount={maxCount}
				onPreview={handlePreview}
				fileList={fileList} // 显示上传过程中所有文件，包括成功和失败的文件
				onChange={({ fileList }) => {
					// 只返回上传成功的文件
					const successFiles = fileList
						.filter(it => !it.status || it.status === 'done')
						.map(it => {
							return {
								...pick(it, ['url', 'uid', 'name']),
								url: getPathname(it?.response?.url || it.url),
							};
						});
					if (onChange && !fileList.some(it => it.status === 'uploading')) {
						onChange(successFiles); // 同步成功文件
					}

					setFileList(fileList);
				}}
				{...rest}>
				{has(props, 'children') ? props?.children : renderChild()}
			</Upload>
			{previewImage && (
				<Image
					wrapperStyle={{ display: 'none' }}
					preview={{
						visible: previewOpen,
						onVisibleChange: visible => setPreviewOpen(visible),
						afterOpenChange: visible => !visible && setPreviewImage(''),
					}}
					src={previewImage}
				/>
			)}
		</>
	);
};

export default OssUpload;
