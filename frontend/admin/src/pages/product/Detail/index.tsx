import React, { useEffect, useState } from 'react';
import http from '@/utils/http';
import { useNavigate, useParams } from 'react-router-dom';
import { ContentLayout, OssUpload, OssUploadProps, ProxyWrapped } from '@yoo/component';
import {
	EDefaultValueType,
	ProEditTable,
	ProForm,
	ProFormItemsFieldType,
} from '@yoo/pro-component';
import {
	EProductArchivedStatus,
	ProductArchivedStatusMap,
	ProductListedStatusMap,
} from '@/constant/product';
import { Button, Card, Form, message, Select, Space, Typography } from 'antd';
import { useUpdate } from 'ahooks';
import { getArchivedProductById } from '@/pages/product/service';
import { PAGES_PRODUCT_DETAIL_URL } from '@/pages/product/pages';
import { transformUrlByRoutePath } from '@/utils/url';
import CategorySearchSelect from '@/component/business/CategorySelect';
import { transformToFields } from '@/utils/transform';
import ImgCrop from 'antd-img-crop';
import { cloneDeep, omit } from 'lodash';

// 递归函数计算笛卡尔积
function generateCombinations(data) {
	// 提取所有 options
	const optionLists = data.map(item => item.options);

	// 笛卡尔积计算
	const cartesianProduct = optionLists.reduce((acc, cur) =>
		acc.flatMap(a => cur.map(b => `${a}/${b}`))
	);

	return cartesianProduct;
}

const ProductDetail: React.FC = () => {
	const params = useParams();
	const { productId } = params;
	const [form] = Form.useForm();
	const forceUpdate = useUpdate();
	const [detailData, setDetailData] = useState<Record<string, any>>({});
	const [loading, setLoading] = useState(false);
	const [editable, setEditable] = useState(true);

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

		const res = await getArchivedProductById(productId).finally(() => {
			setLoading(false);
		});

		const val = {
			...res?.data,
		};

		val.skus = val.skus?.map(it => {
			let sku_parameter = [];
			try {
				sku_parameter = JSON.parse(it?.sku_parameter);
			} catch (e) {
				console.error('sku_parameter parse error', e);
			}
			return {
				...it,
				combination: [],
				sku_parameter: sku_parameter,
			};
		});
		val.sku_parameter = val?.skus?.[0]?.sku_parameter || [];

		form.setFieldsValue(val);

		calculateSku();
		setDetailData(val);
		forceUpdate();
		return res;
	};

	const renderImageEdit = (t: any, r: any) => {
		return (
			<ProxyWrapped>
				{(op: any) => (
					<ImgCrop rotationSlider>
						<OssUpload
							multiple
							listType={'picture-card'}
							{...op}
							onChange={(e: any) => {
								op?.onChange?.(e?.[0]);
							}}
							value={op?.value ? [op?.value] : []}></OssUpload>
					</ImgCrop>
				)}
			</ProxyWrapped>
		);
	};

	// Fields definition, similar to coupon style
	const fields: ProFormItemsFieldType[] = [
		{ label: '商品名称', name: 'name', placeholder: '请输入商品名称' },

		{
			label: '类别',
			name: 'category_id',
			renderFormItem: () => {
				return (
					<ProxyWrapped>
						{config => (
							<CategorySearchSelect
								{...(config as any)}
								onChange={(e, op) => {
									const ob: any = op || {};
									const val = {
										category_id: e,
										category_code: ob?.code,
										category_name: ob?.name,
									};
									form.setFields(transformToFields(val));
									form.validateFields(['category_id']);
								}}
								triggerMode={'open'}
							/>
						)}
					</ProxyWrapped>
				);
			},

			render: (t, r) => {
				return t ? [r.category_code, r.category_name].join('-') : undefined;
			},
		},

		{
			label: '商品主图',
			name: 'main_image',
			fieldProps: { maxCount: 1 } as OssUploadProps,
			valueType: EDefaultValueType.Image,
			renderFormItem: renderImageEdit,
		},
		{
			label: '缩略图',
			name: 'thumbnail_image',
			fieldProps: {
				maxCount: 1,
			} as OssUploadProps,
			valueType: EDefaultValueType.Image,
			renderFormItem: renderImageEdit,
		},
		{
			label: '商品描述',
			name: 'description',
			valueType: EDefaultValueType.Textarea,
			placeholder: '请输入商品详细描述',
		},
		{
			label: '商品简短描述',
			name: 'short_description',
			valueType: EDefaultValueType.Textarea,
			placeholder: '请输入简短描述',
		},
		{
			label: '最低价格',
			name: 'min_price',
			valueType: EDefaultValueType.Money,
			visible: !editable,
		},
		{
			label: '最高价格',
			name: 'max_price',
			valueType: EDefaultValueType.Money,
			visible: !editable,
		},
		// {
		// 	label: 'SKU',
		// 	name: 'sku',
		// 	valueType: EDefaultValueType.Textarea,
		// 	placeholder: '请输入库存单位',
		// },
		// {
		// 	label: 'SEO标题',
		// 	name: 'meta_title',
		// 	placeholder: '请输入SEO标题',
		// },
		// {
		// 	label: 'SEO描述',
		// 	name: 'meta_description',
		// 	valueType: 'textarea',
		// 	placeholder: '请输入SEO描述',
		// },
		// {
		// 	label: 'SEO关键词',
		// 	name: 'meta_keywords',
		// 	placeholder: '请输入SEO关键词',
		// },
	];

	const getFormatSaveData = () => {
		let val = cloneDeep(form.getFieldsValue(true));

		val = omit(val, ['sku_parameter']);
		let formatSkus = val?.skus || [];

		let parameterLen = 0;
		val.skus = formatSkus.map(it => {
			parameterLen = parameterLen + (it?.sku_parameter?.length || 0);
			return {
				...omit(it, ['combination']),
				sku_parameter: JSON.stringify(it?.sku_parameter),
			};
		});
		const minPrice = Math.min(...formatSkus.map(it => it.price));
		const minMarketPrice = Math.max(...formatSkus.map(it => it.market_price));
		val.price = minPrice;
		val.market_price = minMarketPrice;
		//  所有 sku_parameter 大于等2

		val.has_multiple_sku = parameterLen > 1 ? 1 : 0;
		return val;
	};

	// Handle save product data
	const saveHandle = async () => {
		await form.validateFields();

		const val = getFormatSaveData();

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
		const val = getFormatSaveData();
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

	const calculateSku = async () => {
		setTimeout(() => {
			const val = form.getFieldValue('sku_parameter');
			const sku = generateCombinations(val.filter(it => it?.is_sku)) || [];
			const all = generateCombinations(val) || [];
			const oldSku = form.getFieldValue('skus') || [];
			const newSku = sku.map((it, idx) => {
				const find = oldSku.find(
					(i: any) => i.name.split('/').sort().join('/') === it.split('/').sort().join('/')
				);

				//找到相似的参数
				const list = all.filter(childIt => {
					//childIt split 后包含  it的 split
					return it.split('/').every(it => childIt.split('/').includes(it));
				});
				return {
					...find,
					name: it,
					combination: list,
					sku_parameter: val,
				};
			});
			form.setFields([{ name: 'skus', value: newSku }]);
		});
	};

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
			<Form form={form} labelAlign={'right'}>
				<Space direction={'vertical'} className={'w-full'}>
					<Card title={'商品信息'}>
						<ProForm.Items
							labelCol={{ span: 2 }}
							wrapperCol={{ span: 8 }}
							editable={editable}
							fields={fields}
						/>
					</Card>

					<Card title={'商品规格'}>
						<ProEditTable
							name={'sku_parameter'}
							editable={editable}
							columns={[
								{
									title: '规格名称',
									dataIndex: 'name',
									width: 200,
									formItemProps: {
										required: true,
										rules: [{ required: true }],
									},
								},

								{
									title: '规格值',
									dataIndex: 'options',
									formItemProps: {
										required: true,
										rules: [{ required: true }],
										normalize: e => {
											calculateSku();
											return e;
										},
									},
									renderFormItem: () => <Select mode={'tags'} placeholder={'请输入'} />,
								},
								{
									title: '是否SKU参数',
									dataIndex: 'is_sku',
									formItemProps: {
										normalize: e => {
											calculateSku();
											return e;
										},
									},
									valueType: EDefaultValueType.Switch,
								},

								{
									title: '操作',
									dataIndex: '_action',
									visible: editable,
									editable: true,
									width: 200,
									renderFormItem: (t, r, { operations }) => {
										return (
											<a
												onClick={() => {
													operations?.remove?.();
													calculateSku();
												}}>
												删除
											</a>
										);
									},
								},
							]}
						/>
					</Card>

					<Card title={'sku价格'}>
						<ProEditTable
							name={'skus'}
							editable={editable}
							hideAddButton
							columns={[
								{
									title: '规格名称',
									dataIndex: 'name',
									editable: false,
									width: 200,
								},
								{
									title: '组合',
									dataIndex: 'combination',
									width: 200,
									editable: false,

									render: t => {
										return (
											<Typography.Text
												ellipsis={{ tooltip: { title: t?.map(it => <div>{it}</div>) } }}
												style={{
													width: 200,
												}}>
												{t?.join('、 ')}
											</Typography.Text>
										);
									},
								},

								{
									title: '市场价',
									dataIndex: 'market_price',
									formItemProps: {
										required: true,
										rules: [{ required: true }],
									},
									valueType: EDefaultValueType.Money,
								},
								{
									title: '采购价',
									dataIndex: 'purchase_price',
									formItemProps: {
										required: true,
										rules: [{ required: true }],
									},
									valueType: EDefaultValueType.Money,
								},
								{
									title: '售卖价',
									dataIndex: 'price',
									formItemProps: {
										required: true,
										rules: [{ required: true }],
									},
									valueType: EDefaultValueType.Money,
								},
							]}
						/>
					</Card>
				</Space>
			</Form>
		</ContentLayout>
	);
};

export default ProductDetail;
