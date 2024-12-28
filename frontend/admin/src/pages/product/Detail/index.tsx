import React, { useEffect, useState } from 'react';
import http from '@/utils/http';
import { useParams } from 'react-router-dom';
import ContentLayout from '@/component/ContentLayout';
import { EDefaultValueType, ProForm, ProFormItemsFieldType } from '@yoohoo/pro-component';
import {
	EProductArchivedStatus,
	ProductArchivedStatusMap,
	ProductListedStatusMap,
	ProductStatusMap,
} from '@/constant/product';
import { Button, Card, Form, message, Space } from 'antd';
import { useUpdate } from 'ahooks';
import { OssUploadProps } from '@/component/OssUpload';

const UserDetail: React.FC = props => {
	const params = useParams();
	const { productId } = params;
	const [form] = Form.useForm();
	const forceUpdate = useUpdate();
	const [detailData, setDetailData] = useState<Record<string, any>>({});
	const [loading, setLoading] = useState(false);
	const fetchDetailData = async () => {
		setLoading(true);
		const res = await http.get('/admin/product/' + productId).finally(() => {
			setLoading(false);
		});
		form.setFieldsValue({
			...res?.data,
		});
		setDetailData({ ...res?.data });
		forceUpdate();
		return res; // 假设返回值中包含 token
	};
	const [readonly, setReadonly] = useState(true);

	const fields: ProFormItemsFieldType[] = [
		{ label: '商品名称', name: 'name', placeholder: '请输入商品名称' },
		{
			label: '建档状态',
			name: 'archived_status',
			valueType: EDefaultValueType.EnumStatusTag,
			valueEnum: ProductArchivedStatusMap,
			hidden: !readonly,
		},
		{
			label: '商品描述',
			name: 'description',
			valueType: EDefaultValueType.Textarea,
			placeholder: '请输入商品描述',
		},
		{
			label: '商品简短描述',
			name: 'short_description',
			valueType: EDefaultValueType.Textarea,
			placeholder: '请输入简短描述',
		},

		{
			label: '商品主图',
			name: 'main_image',
			fieldProps: {
				maxCount: 1,
			} as OssUploadProps,
			valueType: EDefaultValueType.Image,
		},
		{
			label: '缩略图',
			name: 'thumbnail_image',
			fieldProps: {
				maxCount: 1,
			} as OssUploadProps,
			valueType: EDefaultValueType.Image,
		},
		{
			label: '轮播图',
			name: 'carousel_images',
			fieldProps: {
				maxCount: 5,
			} as OssUploadProps,
			valueType: EDefaultValueType.Image,
		},
		{
			label: '其他图片',
			name: 'other_images',
			fieldProps: {
				maxCount: 5,
			} as OssUploadProps,
			valueType: EDefaultValueType.Image,
		},

		{ label: '商品价格', name: 'price', valueType: 'money', placeholder: '请输入价格' },
		{ label: '成本价格', name: 'cost_price', valueType: 'money', placeholder: '请输入成本价格' },
		{
			label: 'SKU',
			name: 'sku',
			valueType: EDefaultValueType.Textarea,
			placeholder: '请输入库存单位',
		},
		{
			label: '商品状态',
			name: 'status',
			valueEnum: ProductStatusMap,
			hidden: !readonly,
		},

		{
			label: '上架状态',
			name: 'listed_status',
			valueEnum: ProductListedStatusMap,
			hidden: !readonly,
		},
		{
			label: '创建时间',
			name: 'created_at',
			valueType: EDefaultValueType.DateTime,
			readonly: true,
		},
		{
			label: '更新时间',
			name: 'update_at',
			valueType: EDefaultValueType.DateTime,
			readonly: true,
			hidden: !readonly,
		},
		{ label: 'SEO标题', name: 'meta_title', placeholder: '请输入SEO标题' },
		{
			label: 'SEO描述',
			name: 'meta_description',
			valueType: 'textarea',
			placeholder: '请输入SEO描述',
		},
		{ label: 'SEO关键词', name: 'meta_keywords', placeholder: '请输入SEO关键词' },
		{ label: '创建人名称', name: 'created_by_name', hidden: !readonly },
		{ label: '更新人名称', name: 'updated_by_name', hidden: !readonly },
	];

	const saveHandle = async () => {
		await form.validateFields();
		const val = form.getFieldsValue(true);
		setLoading(true);
		const res = await http.put('/admin/product/' + productId, val).finally(() => {
			setLoading(false);
		});
		if (res.success) {
			message.success('保存成功');
			fetchDetailData();
		}
	};

	useEffect(() => {
		fetchDetailData();
	}, []);
	const submitHandle = async () => {
		await form.validateFields();
		const val = form.getFieldsValue(true);
		setLoading(true);
		const res = await http.put('/admin/product/' + productId + '/submit', val).finally(() => {
			setLoading(false);
		});
		if (res.success) {
			message.success('提交成功');
			if (!readonly) {
				setReadonly(true);
			}
			fetchDetailData();
		}
	};
	const rejectHandle = async () => {
		setLoading(true);
		const res = await http
			.put('/admin/product/' + productId + '/reject', {
				rejection_reason: '审核拒绝',
			})
			.finally(() => {
				setLoading(false);
			});
		if (res.success) {
			message.success('操作成功');
			fetchDetailData();
		}
	};

	const approvalHandle = async () => {
		setLoading(true);
		const res = await http.put('/admin/product/' + productId + '/approve').finally(() => {
			setLoading(false);
		});
		if (res.success) {
			message.success('操作成功');
			fetchDetailData();
		}
	};

	const renderExtra = () => {
		const editEl = readonly ? (
			<Button
				onClick={() => {
					setReadonly(false);
				}}>
				编辑
			</Button>
		) : (
			<>
				<Button
					onClick={() => {
						setReadonly(true);
					}}>
					取消
				</Button>
				{detailData?.archived_status <= EProductArchivedStatus.Draft ? (
					<Button type={'primary'} onClick={() => saveHandle()}>
						保存草稿
					</Button>
				) : null}
			</>
		);

		return (
			<Space>
				{detailData?.archived_status <= EProductArchivedStatus.Draft ||
					(detailData?.archived_status <= EProductArchivedStatus.Rejected && editEl)}

				{detailData?.archived_status <= EProductArchivedStatus.Draft ? (
					<Button type={'primary'} onClick={() => submitHandle()}>
						提交审核
					</Button>
				) : null}

				{detailData?.archived_status === EProductArchivedStatus.PendingApproval ? (
					<Button type={'primary'} danger onClick={() => rejectHandle()}>
						审核拒绝
					</Button>
				) : null}
				{detailData?.archived_status === EProductArchivedStatus.PendingApproval ? (
					<Button type={'primary'} onClick={() => approvalHandle()}>
						审核通过
					</Button>
				) : null}

				{detailData?.archived_status === EProductArchivedStatus.Rejected ? (
					<Button type={'primary'} onClick={() => submitHandle()}>
						重新提交审核
					</Button>
				) : null}
			</Space>
		);
	};

	return (
		<ContentLayout loading={loading}>
			<Form
				form={form}
				labelCol={{
					span: 4,
				}}
				wrapperCol={{
					span: 7,
				}}>
				<Card title={'基础信息'} extra={renderExtra()}>
					<ProForm.Items readonly={readonly} fields={fields} />
				</Card>
			</Form>
		</ContentLayout>
	);
};
export default UserDetail;
