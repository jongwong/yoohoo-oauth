import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import { EDefaultValueType, ProForm, ProFormItemsFieldType } from '@yoo/pro-component';
import { useUpdate } from 'ahooks';
import { Button, Card, Form, message, Space } from 'antd';

import { ContentLayout } from '@yoo/component';
import { GlobalEnableTypeMap } from '@/constant/common';
import { CategoryLevelMap } from '@/constant/product_category';
import { PAGES_PRODUCT_CATEGORY_DETAIL_URL } from '@/pages/product-category/pages';
import {
	createProductCategory,
	getProductCategoryById,
	updateProductCategory,
	updateProductCategoryDisable,
	updateProductCategoryEnable,
} from '@/pages/product-category/service';
import { transformUrlByRoutePath } from '@/utils/url';

const Detail: React.FC = () => {
	const params = useParams();
	const { categoryId } = params as { categoryId: string };
	const [form] = Form.useForm();
	const forceUpdate = useUpdate();
	const [detailData, setDetailData] = useState<Record<string, any>>({});
	const [loading, setLoading] = useState(false);
	const [editable, setEditable] = useState(false);

	const navigator = useNavigate();

	// Fetch category details
	const fetchDetailData = async () => {
		if (!categoryId) {
			setEditable(true);
			return;
		}
		setLoading(true);

		const res = await getProductCategoryById(categoryId).finally(() => {
			setLoading(false);
		});

		form.setFieldsValue({
			...res?.data,
		});
		setDetailData({ ...res?.data });
		setEditable(false);
		forceUpdate();
		return res;
	};

	const fields: ProFormItemsFieldType[] = [
		{
			label: '类别名称',
			name: 'name',
			placeholder: '请输入类别名称',
			formItemProps: {
				rules: [{ required: true }],
			},
		},
		{
			label: '类别代码',
			name: 'code',
			placeholder: '请输入类别代码',
			formItemProps: {
				rules: [{ required: true }],
			},
		},
		{
			label: '类别描述',
			name: 'description',
			valueType: EDefaultValueType.Textarea,
			placeholder: '请输入类别描述',
		},
		{
			label: '类别层级',
			name: 'level',
			valueEnum: CategoryLevelMap,
			placeholder: '请选择类别层级',
			formItemProps: {
				rules: [{ required: true }],
			},
		},
	];

	// Handle save category data
	const saveHandle = async () => {
		await form.validateFields();
		const val = form.getFieldsValue(true);
		setLoading(true);
		const fn = categoryId ? updateProductCategory(categoryId, val) : createProductCategory(val);
		const res = await fn.finally(() => {
			setLoading(false);
		});
		if (res.success) {
			message.success('保存成功');
			if (!categoryId) {
				navigator(transformUrlByRoutePath(PAGES_PRODUCT_CATEGORY_DETAIL_URL, res.data.id));
				setEditable(false);
				return;
			} else {
				setEditable(false);
				fetchDetailData();
			}
		}
	};

	// Render extra buttons
	const renderExtra = () => {
		const editEl = !editable ? (
			<>
				{detailData.enable === 0 ? (
					<Button
						onClick={async () => {
							setLoading(true);
							const res = await updateProductCategoryEnable(categoryId).finally(() => {
								setLoading(false);
							});
							if (res.success) {
								message.success('操作成功');
								fetchDetailData();
							}
						}}>
						启用
					</Button>
				) : (
					<Button
						danger
						onClick={async () => {
							setLoading(true);
							const res = await updateProductCategoryDisable(categoryId).finally(() => {
								setLoading(false);
							});
							if (res.success) {
								message.success('操作成功');
								fetchDetailData();
							}
						}}>
						停用
					</Button>
				)}
				<Button onClick={() => setEditable(true)}>编辑</Button>
			</>
		) : (
			<>
				<Button onClick={() => setEditable(false)}>取消</Button>
				<Button onClick={saveHandle} type={'primary'}>
					保存
				</Button>
			</>
		);

		return <Space>{editEl}</Space>;
	};

	useEffect(() => {
		fetchDetailData();
	}, []);

	return (
		<ContentLayout
			loading={loading}
			header={{
				extra: renderExtra(),
				info: categoryId
					? {
							data: detailData,
							leftItems: [
								{ label: '创建人', name: 'created_by_name' },
								{
									label: '创建时间',
									name: 'created_at',
									valueType: EDefaultValueType.DateTimeMinutes,
								},
								{ label: '更新人', name: 'updated_by_name' },
								{
									label: '更新时间',
									name: 'updated_at',
									valueType: EDefaultValueType.DateTimeMinutes,
								},
							],
							rightItems: [
								{
									label: '是否开启',
									name: 'enable',
									valueEnum: GlobalEnableTypeMap,
									valueType: EDefaultValueType.EnumStatusText,
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

export default Detail;
