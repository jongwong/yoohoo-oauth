import React, { useEffect, useState } from 'react';
import http from '@/utils/http';
import { useNavigate, useParams } from 'react-router-dom';
import ContentLayout from '@yoo/component';
import { EDefaultValueType, ProForm, ProFormItemsFieldType } from '@yoo/pro-component';
import {
	EProductArchivedStatus,
	ProductArchivedStatusMap,
	ProductListedStatusMap,
	ProductStatusMap,
} from '@/constant/product';
import { Button, Card, Form, message, Space } from 'antd';
import { useUpdate } from 'ahooks';
import { OssUploadProps } from '@/component/OssUpload';
import { getProductById } from '@/pages/product/service';
import { PAGES_PRODUCT_DETAIL_URL } from '@/pages/product/pages';
import { transformUrlByRoutePath } from '@/utils/url';

const ProductDetail: React.FC = () => {
	const params = useParams();
	const { productId } = params;
	const [form] = Form.useForm();
	const forceUpdate = useUpdate();
	const [detailData, setDetailData] = useState<Record<string, any>>({});
	const [loading, setLoading] = useState(false);
	const [editable, setEditable] = useState(false);

	const navigator = useNavigate();
	// Fetch product details
	const fetchDetailData = async () => {
		if (!productId) {
			setDetailData({
				archived_status: EProductArchivedStatus.Draft,
			});
			setEditable(true);
			return;
		}
		setLoading(true);

		const res = await getProductById(productId).finally(() => {
			setLoading(false);
		});
		form.setFieldsValue({
			...res?.data,
		});
		setDetailData({ ...res?.data });
		forceUpdate();
		return res;
	};

	// Fields definition, similar to coupon style
	const fields: ProFormItemsFieldType[] = [
		{ label: '商品名称', name: 'name', placeholder: '请输入商品名称' },
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
			fieldProps: { maxCount: 1 } as OssUploadProps,
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
		{
			label: '商品价格',
			name: 'price',
			valueType: 'money',
			placeholder: '请输入价格',
		},
		{
			label: '成本价格',
			name: 'cost_price',
			valueType: 'money',
			placeholder: '请输入成本价格',
		},
		{
			label: 'SKU',
			name: 'sku',
			valueType: EDefaultValueType.Textarea,
			placeholder: '请输入库存单位',
		},
		{
			label: 'SEO标题',
			name: 'meta_title',
			placeholder: '请输入SEO标题',
		},
		{
			label: 'SEO描述',
			name: 'meta_description',
			valueType: 'textarea',
			placeholder: '请输入SEO描述',
		},
		{
			label: 'SEO关键词',
			name: 'meta_keywords',
			placeholder: '请输入SEO关键词',
		},
	];

	// Handle save product data
	const saveHandle = async () => {
		await form.validateFields();
		const val = form.getFieldsValue(true);
		setLoading(true);
		const fn = productId
			? http.put('/admin/product/' + productId, val)
			: http.post('/admin/product', val);
		const res = await fn.finally(() => {
			setLoading(false);
		});
		if (res.success) {
			message.success('保存成功');
			if (!productId) {
				navigator(transformUrlByRoutePath(PAGES_PRODUCT_DETAIL_URL, res.data.id));
			}

			fetchDetailData();
		}
	};

	// Handle submit product for approval
	const submitHandle = async () => {
		await form.validateFields();
		const val = form.getFieldsValue(true);
		setLoading(true);

		const res = await http.put('/admin/product/' + productId + '/submit', val).finally(() => {
			setLoading(false);
		});
		if (res.success) {
			message.success('提交成功');
			setEditable(false);
			fetchDetailData();
		}
	};

	// Handle reject product
	const rejectHandle = async () => {
		setLoading(true);
		const res = await http
			.put('/admin/product/' + productId + '/reject', { rejection_reason: '审核拒绝' })
			.finally(() => {
				setLoading(false);
			});
		if (res.success) {
			message.success('操作成功');
			fetchDetailData();
		}
	};

	// Handle approve product
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

	// Render extra buttons based on status
	const renderExtra = () => {
		const editEl = !editable ? (
			<Button onClick={() => setEditable(true)}>编辑</Button>
		) : (
			<>
				<Button onClick={() => setEditable(false)}>取消</Button>
				{detailData?.archived_status <= EProductArchivedStatus.Draft ? (
					<Button onClick={saveHandle}>保存</Button>
				) : null}
			</>
		);

		return (
			<Space>
				{(detailData?.archived_status <= EProductArchivedStatus.Draft ||
					detailData?.archived_status === EProductArchivedStatus.Rejected) &&
					editEl}

				{productId && detailData?.archived_status <= EProductArchivedStatus.Draft && (
					<Button type={'primary'} onClick={submitHandle}>
						提交审核
					</Button>
				)}

				{detailData?.archived_status === EProductArchivedStatus.PendingApproval && (
					<Space>
						<Button type={'primary'} danger onClick={rejectHandle}>
							审核拒绝
						</Button>
						<Button type={'primary'} onClick={approvalHandle}>
							审核通过
						</Button>
					</Space>
				)}

				{detailData?.archived_status === EProductArchivedStatus.Rejected && (
					<Button type={'primary'} onClick={submitHandle}>
						重新提交审核
					</Button>
				)}
			</Space>
		);
	};

	useEffect(() => {
		fetchDetailData();
	}, []);

	return (
		<ContentLayout
			loading={loading}
			header={{
				extra: renderExtra(),
				info: productId
					? {
							data: detailData,
							leftItems: [
								{ label: '创建人名称', name: 'created_by_name' },
								{ label: '创建时间', name: 'created_at', valueType: EDefaultValueType.DateTime },
								{ label: '更新人名称', name: 'updated_by_name' },
								{ label: '更新时间', name: 'updated_at', valueType: EDefaultValueType.DateTime },
							],
							rightItems: [
								{ label: '商品状态', name: 'status', valueEnum: ProductStatusMap },
								{ label: '上架状态', name: 'listed_status', valueEnum: ProductListedStatusMap },
								{
									label: '建档状态',
									name: 'archived_status',
									valueEnum: ProductArchivedStatusMap,
									valueType: EDefaultValueType.EnumStatusTag,
								},
							],
					  }
					: undefined,
			}}>
			<Form form={form} labelCol={{ span: 4 }} wrapperCol={{ span: 7 }}>
				<Card>
					<ProForm.Items editable={editable} fields={fields} />
				</Card>
			</Form>
		</ContentLayout>
	);
};

export default ProductDetail;
