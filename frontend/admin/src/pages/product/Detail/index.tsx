import React, { useEffect, useState } from 'react';
import http from '@/utils/http';
import { useParams } from 'react-router-dom';
import ContentLayout from '@/component/ContentLayout';
import { EDefaultValueType, ProForm, ProFormItemsFieldType } from '@yoohoo/pro-component';
import {
	ProductArchivedStatusMap,
	ProductListedStatusMap,
	ProductStatusMap,
} from '@/constant/product';
import { Button, Card, Form, Space } from 'antd';
import { useUpdate } from 'ahooks';

const UserDetail: React.FC = props => {
	const params = useParams();
	const { productId } = params;
	const [form] = Form.useForm();
	const forceUpdate = useUpdate();
	const fetchDetailData = async () => {
		const res = await http.get('/admin/product/' + productId);

		form.setFieldsValue(res.data);
		forceUpdate();
		return res; // 假设返回值中包含 token
	};
	const [readonly, setReadonly] = useState(true);

	useEffect(() => {
		fetchDetailData();
	}, []);
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
			name: 'shortDescription',
			valueType: EDefaultValueType.Textarea,
			placeholder: '请输入简短描述',
		},
		{ label: '商品价格', name: 'price', valueType: 'money', placeholder: '请输入价格' },
		{ label: '成本价格', name: 'costPrice', valueType: 'money', placeholder: '请输入成本价格' },
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
		},
		{ label: '创建时间', name: 'createdAt', valueType: EDefaultValueType.DateTime, readonly: true },
		{
			label: '更新时间',
			name: 'upLocalDateTimedAt',
			valueType: EDefaultValueType.DateTime,
			readonly: true,
		},
		{ label: 'SEO标题', name: 'metaTitle', placeholder: '请输入SEO标题' },
		{
			label: 'SEO描述',
			name: 'metaDescription',
			valueType: 'textarea',
			placeholder: '请输入SEO描述',
		},
		{ label: 'SEO关键词', name: 'metaKeywords', placeholder: '请输入SEO关键词' },
		{
			label: '建档状态',
			name: 'archivedStatus',
			valueEnum: ProductArchivedStatusMap,
		},
		{
			label: '上架状态',
			name: 'listedStatus',
			valueEnum: ProductListedStatusMap,
		},
		{ label: '创建人名称', name: 'createdByName', hidden: !readonly },
		{ label: '更新人名称', name: 'updatedByName', hidden: !readonly },
	];

	const renderExtra = () => {
		if (readonly) {
			return (
				<Button
					type={'primary'}
					onClick={() => {
						setReadonly(false);
					}}>
					编辑
				</Button>
			);
		}
		return (
			<Space>
				<Button
					onClick={() => {
						setReadonly(true);
					}}>
					取消
				</Button>
				<Button type={'primary'}>保存</Button>
			</Space>
		);
	};
	return (
		<ContentLayout>
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
