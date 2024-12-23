import React, { useRef } from 'react';
import { Button, message, Upload, UploadProps } from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import OSS from 'ali-oss';
import http from '@/utils/http';
import { has } from 'lodash'; // 请确保路径正确

function isTokenExpired(expiration: number) {
	const expirationTime = new Date(expiration * 1000); // expiration 是 Unix 时间戳，单位为秒
	const currentTime = new Date();
	return currentTime >= expirationTime;
}

const OssUpload: React.FC<
	UploadProps & {
		readOnly?: boolean;
		value?: any;
	}
> = props => {
	const clientRef = useRef<any>();
	const credentialsRef = useRef<any>(null);

	const refreshCredentials = async () => {
		const response = await http.get('/admin/oss/token');

		const credentials: any = response.data;
		credentialsRef.current = credentials;

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

	const customRequest = async options => {
		const { file, onProgress, onSuccess, onError } = options;
		let credentials = getCredentials();

		// 每次上传前检查凭证是否过期
		if (!credentials || isTokenExpired(credentials?.expiration)) {
			await refreshCredentials(); // 刷新凭证
		}
		credentials = getCredentials();
		const client = clientRef.current;

		if (!client || !credentials) {
			message.error('OSS没有初始化好');
			return onError(new Error('OSS没有初始化好'));
		}

		const fileName = `/dev/${file.name}`;
		try {
			// 使用multipartUpload进行分片上传
			await client.multipartUpload(fileName, file, {
				partSize: 10 * 1024 * 1024, // 每个分片最大10MB
				progress: p => {
					// 上传进度，更新进度条
					onProgress({ percent: p.percent }, file);
					console.log(`上传进度: ${p.percent}%`);
				},
			});
			onSuccess({}, file); // 上传成功的回调
		} catch (error) {
			message.error('文件上传失败');
			console.error(error);
			onError(error); // 上传失败的回调
		}
	};

	// 获取凭证
	const getCredentials = () => {
		return credentialsRef.current;
	};

	return (
		<Upload customRequest={customRequest} {...props}>
			{has(props, 'children') ? props?.children : <Button icon={<UploadOutlined />}>上传</Button>}
		</Upload>
	);
};

export default OssUpload;
