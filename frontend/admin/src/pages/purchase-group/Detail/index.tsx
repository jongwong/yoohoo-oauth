import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import { ContentLayout } from '@yoo/component';
import { EDefaultValueType, ProForm, ProFormItemsFieldType } from '@yoo/pro-component';
import { useUpdate } from 'ahooks';
import { Button, Card, Form, message, Space } from 'antd';
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
import ProEditTable from '@/component/pro-component/ProEditTable';

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

		form.setFieldsValue({ ...res?.data });
		setDetailData({ ...res?.data });
		setEditable(false);
		forceUpdate();
		return res;
	};

	const fields: ProFormItemsFieldType[] = [
		{
			label: '团购名称',
			name: 'name',
			placeholder: '请输入团购名称',
			formItemProps: {
				rules: [{ required: true }],
			},
		},
		{
			label: '团购描述',
			name: 'description',
			valueType: EDefaultValueType.Textarea,
			placeholder: '请输入团购描述',
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
			label: '开始配送时间',
			name: 'time_delivery_start',
			valueType: EDefaultValueType.DateTime,
			placeholder: '请选择开始配送时间',
		},
		{
			label: '配送截止时间',
			name: 'time_delivery_end',
			valueType: EDefaultValueType.DateTime,
			placeholder: '请选择配送截止时间',
		},
		{
			label: '团购开始时间',
			name: 'time_start',
			valueType: EDefaultValueType.DateTime,
			placeholder: '请选择团购开始时间',
		},
		{
			label: '团购结束时间',
			name: 'time_end',
			valueType: EDefaultValueType.DateTime,
			placeholder: '请选择团购结束时间',
		},
	];

	const saveHandle = async () => {
		await form.validateFields();
		const val = form.getFieldsValue(true);
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
								dataIndex: 'name',
								formItemProps: {
									rules: [{ required: true }],
								},
							},
							{
								title: '商品数量',
								dataIndex: 'quantity',
							},
						]}
					/>
				</Card>
			</Form>
		</ContentLayout>
	);
};

export default Detail;
