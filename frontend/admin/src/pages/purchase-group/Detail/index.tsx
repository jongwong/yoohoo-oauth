import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import { ContentLayout, OssUpload, ProxyWrapped } from '@yoo/component';
import {
	EDefaultValueType,
	ProEditTable,
	ProForm,
	ProFormItemsFieldType,
} from '@yoo/pro-component';
import { useUpdate } from 'ahooks';
import { Button, Card, DatePicker, Form, Image, message, Space, Typography } from 'antd';

import ProductSearchSelect from '@/component/business/ProductSearchSelect';
import { GlobalEnableTypeMap } from '@/constant/common';
import { PurchaseGroupStatusMap } from '@/constant/purchase-group';

import { PAGES_PURCHASE_GROUP_DETAIL_URL } from '../pages';
import {
	createPurchaseGroup,
	getPurchaseGroupById,
	updatePurchaseGroup,
	updatePurchaseGroupDisable,
	updatePurchaseGroupEnable,
} from '../service';
import DistributionPointSelect from '@/component/business/DistributionPointSelect';
import CardContainer from '@/component/base-ui/CardContainer';
import { RangePickerProps } from 'antd/es/date-picker';
import dayjs from 'dayjs';
import { transformToFields } from '@/utils/transform';
import DeliveryTimeRangePicker from '@/pages/purchase-group/component/DeliveryTimeRangePicker';
import ImgCrop from 'antd-img-crop';
import { generateFileUrl } from '@/utils/file';

const RangesTimeComponent: React.FC = () => {
	return <div>oo</div>;
};

const generateTimeRanges = () => {
	// 定义默认时间范围
	const timeRanges = [
		{ from: '08:15', to: '10:15' },
		{ from: '11:45', to: '13:30' },
		{ from: '17:45', to: '18:15' },
	];

	// 转换为 dayjs 对象
	return timeRanges.map(range => ({
		from: dayjs()
			.hour(Number(range.from.split(':')[0]))
			.minute(Number(range.from.split(':')[1])),
		to: dayjs()
			.hour(Number(range.to.split(':')[0]))
			.minute(Number(range.to.split(':')[1])),
	}));
};

const Detail: React.FC = () => {
	const params = useParams();
	const { groupId } = params as { groupId: string };
	const [form] = Form.useForm<any>();
	const forceUpdate = useUpdate();
	const [detailData, setDetailData] = useState<Record<string, any>>({});
	const [loading, setLoading] = useState(false);
	const [editable, setEditable] = useState(false);

	const navigator = useNavigate();

	const fetchDetailData = async () => {
		if (!groupId) {
			setEditable(true);
			return;
		}
		setLoading(true);

		const res = await getPurchaseGroupById(groupId).finally(() => {
			setLoading(false);
		});

		const val = res?.data || {};
		val.img_url = val.img_url
			? [
					{
						url: val?.img_url,
						name: val?.img_url,
					},
			  ]
			: undefined;
		form.setFieldsValue({ ...val });
		setDetailData({ ...val });
		setEditable(false);
		forceUpdate();
		return res;
	};

	const generateName = (addressName = '', time?: number) => {
		// 地址名称 + 送餐时间的日期加小时
		return addressName + dayjs(time).format('YYYYMMDDHH');
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
								op?.onChange?.(e);
							}}
							value={Array.isArray(op?.value) ? op?.value : []}></OssUpload>
					</ImgCrop>
				)}
			</ProxyWrapped>
		);
	};
	const fields: ProFormItemsFieldType[] = [
		{
			label: '团购名称',
			name: 'name',
			placeholder: '请输入团购名称',
			formItemProps: {
				rules: [{ required: true }],
			},
			fieldProps: {
				disabled: true,
			},
		},
		{
			label: '配送地址',
			name: 'distribution_point_id',
			formItemProps: {
				getValueFromEvent: (e, op) => {
					form.setFields(
						transformToFields({
							distribution_point_name: op.name,
							name: generateName(op.name, form.getFieldValue('time_delivery_start')),
						})
					);
					return e;
				},
			},
			renderFormItem: () => {
				return <DistributionPointSelect triggerMode={'open'} />;
			},

			render: (t, r) => {
				return r?.distribution_point_name;
			},
		},

		{
			label: '团购描述',
			name: 'description',
			valueType: EDefaultValueType.Textarea,
			placeholder: '请输入团购描述',
		},
		{
			label: '最小成团人数',
			name: 'group_required_count',
			placeholder: '请输入最小成团人数',
			formItemProps: {
				initialValue: 1,
				rules: [{ required: true, type: 'number', min: 1 }],
			},
			valueType: EDefaultValueType.PositiveInteger,
		},

		{
			label: '最大参与人数',
			name: 'max_participants',
			placeholder: '请输入最大参与人数',
			formItemProps: {
				rules: [{ required: true, type: 'number', min: 1 }],
			},
			valueType: EDefaultValueType.PositiveInteger,
		},
		{
			label: '当前参与人数',
			name: 'current_participants',
			valueType: EDefaultValueType.PositiveInteger,
			visible: !editable,
		},
		{
			label: '是否启用',
			name: 'enable',
			valueEnum: GlobalEnableTypeMap,
			visible: !editable,
		},
		{
			label: '团购时间',
			name: 'time_start',
			fieldProps: {
				showTime: true,
			} as RangePickerProps,
			render: (t, r) =>
				t && r?.time_end
					? [r.time_start, r.time_end].map(it => dayjs(it).format('YYYY-DD-MM HH:mm')).join(' ~ ')
					: undefined,
			renderFormItem: (t, _r, opt) => {
				return (
					<ProxyWrapped>
						{inputProps => {
							const r = form.getFieldsValue(true);
							const val: any = [
								r?.time_start ? dayjs(r?.time_start) : undefined,
								r?.time_end ? dayjs(r?.time_end) : undefined,
							];
							return (
								<DatePicker.RangePicker
									{...inputProps}
									value={val as any}
									format="YYYY-MM-DD HH:mm:ss"
									onChange={e => {
										form.setFields([
											{
												name: 'time_start',
												value: e?.[0]?.startOf('day').valueOf(),
											},
											{
												name: 'time_end',
												value: e?.[1]?.endOf('day').valueOf(),
											},
										]);
										// validateFields 是为form item 的validateTrigger,onChange触发校验
										form.validateFields([['time_start']]);
									}}
								/>
							);
						}}
					</ProxyWrapped>
				);
			},
		},
		{
			label: '配送时间',
			name: 'time_delivery_start',
			formItemProps: {
				normalize: e => {
					return e;
				},
			},
			render: (t, r) =>
				t && r?.time_delivery_end
					? [r.time_delivery_start, r.time_delivery_end]
							.map(it => dayjs(it).format('YYYY-DD-MM HH:mm'))
							.join(' ~ ')
					: undefined,
			renderFormItem: (t, r) => {
				return (
					<ProxyWrapped>
						{config => {
							const endN = form.getFieldValue('time_delivery_end');
							const val = config.value ? dayjs(config.value) : undefined;
							const end = endN ? dayjs(endN) : undefined;

							return (
								<DeliveryTimeRangePicker
									generateTimeRanges={generateTimeRanges}
									value={val && end ? [val, end] : undefined}
									onChange={([s, e]) => {
										form.setFields(
											transformToFields({
												time_delivery_start: s?.valueOf(),
												time_delivery_end: e?.valueOf(),
												name: generateName(
													form.getFieldValue('distribution_point_name'),
													s?.valueOf()
												),
											})
										);
									}}
								/>
							);
						}}
					</ProxyWrapped>
				);
			},
		},
		{
			label: '团购图片',
			name: 'img_url',
			valueType: EDefaultValueType.Image,
			renderFormItem: renderImageEdit,
		},
	];

	const saveHandle = async () => {
		await form.validateFields();
		const val = form.getFieldsValue(true);

		val.img_url = val?.img_url?.[0]?.url;
		setLoading(true);
		const fn = groupId ? updatePurchaseGroup(groupId, val) : createPurchaseGroup(val);
		const res = await fn.finally(() => {
			setLoading(false);
		});
		if (res.success) {
			message.success('保存成功');
			if (!groupId) {
				navigator(PAGES_PURCHASE_GROUP_DETAIL_URL.replace(':groupId', res.data.id));
				setEditable(false);
				return;
			} else {
				setEditable(false);
				fetchDetailData();
			}
		}
	};

	const renderExtra = () => {
		const editEl = !editable ? (
			<>
				{detailData.enable === 0 ? (
					<Button
						onClick={async () => {
							setLoading(true);
							const res = await updatePurchaseGroupEnable(groupId).finally(() => {
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
							const res = await updatePurchaseGroupDisable(groupId).finally(() => {
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
				info: groupId
					? {
							data: detailData,
							leftItems: [
								{
									label: '团购名称',
									name: 'name',
								},
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
								{
									label: '状态',
									name: 'status',
									valueEnum: PurchaseGroupStatusMap,
								},
							],
					  }
					: undefined,
			}}>
			<Form form={form} labelCol={{ span: 4 }} wrapperCol={{ span: 7 }}>
				<CardContainer>
					<Card title={'基础信息'}>
						<ProForm.Items fields={fields} editable={editable} />
					</Card>

					<Card title={'商品信息'}>
						<ProEditTable
							name={'products'}
							editable={editable}
							columns={[
								{
									title: '商品名称',
									dataIndex: 'product_id',
									formItemProps: {
										rules: [{ required: true }],
										getValueFromEvent: (e, op) => {
											return e;
										},
									},
									renderFormItem: (t, r) => {
										return <ProductSearchSelect className={'w-1-1'} />;
									},
									render: (t, r) => {
										return [r.product_code, r.product_name].join(':');
									},
									width: '20%',
								},
								{
									title: '商品图片',
									dataIndex: 'image_url',
									editable: false,
									visible: !editable,
									width: '20%',
									render: (t, r) => {
										return <Image width={80} height={80} src={generateFileUrl(t)} />;
									},
								},

								{
									title: '售卖价格',
									dataIndex: 'price',
									width: '20%',
									valueType: EDefaultValueType.Money,
								},
								{
									title: '商品减价',
									dataIndex: 'amount_offset',
									width: '20%',
									valueType: EDefaultValueType.Money,
								},

								{
									title: '最大库存',
									dataIndex: 'max_stock',
									width: editable ? '20%' : '20%',
									valueType: EDefaultValueType.PositiveInteger,
								},

								{
									title: '操作',
									dataIndex: '_action',
									editable: false,
									visible: editable,
									width: 120,
									render: (t, r, idx, { operations }) => {
										return (
											<Space>
												<Typography.Link
													onClick={() => {
														operations.remove?.();
													}}>
													删除
												</Typography.Link>
											</Space>
										);
									},
								},
							]}
						/>
					</Card>
				</CardContainer>
			</Form>
		</ContentLayout>
	);
};

export default Detail;
