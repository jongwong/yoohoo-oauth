import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import ContentLayout from '@/component/ContentLayout';
import { EDefaultValueType, ProForm, ProFormItemsFieldType } from '@yoohoo/pro-component';
import { Button, Card, Form, message, Space } from 'antd';
import { useUpdate } from 'ahooks';
import {
	createDistributionPoint,
	getDistributionPointById,
	updateDistributionPoint,
	updateDistributionPointDisable,
	updateDistributionPointEnable,
} from '@/pages/distribution-point/service';
import { PAGES_DISTRIBUTION_POINT_DETAIL_URL } from '@/pages/distribution-point/pages';
import { transformUrlByRoutePath } from '@/utils/url';
import {
	DistributionPointEnableMap,
	EDistributionPointEnable,
} from '@/constant/distribution-point';

const DistributionPointDetail: React.FC = () => {
	const params = useParams();
	const { distributionPointId } = params as {
		distributionPointId: string;
	};
	const [form] = Form.useForm();
	const forceUpdate = useUpdate();
	const [detailData, setDetailData] = useState<Record<string, any>>({});
	const [loading, setLoading] = useState(false);
	const [editable, setEditable] = useState(false);

	const navigator = useNavigate();
	// Fetch distribution point details
	const fetchDetailData = async () => {
		if (!distributionPointId) {
			setEditable(true);
			return;
		}
		setLoading(true);

		const res = await getDistributionPointById(distributionPointId).finally(() => {
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
			label: '配送点名称',
			name: 'name',
			placeholder: '请输入配送点名称',
			formItemProps: {
				rules: [{ required: true }],
			},
		},
		{
			label: '配送点地址',
			name: 'address',
			placeholder: '请输入配送点地址',
			formItemProps: {
				rules: [{ required: true }],
			},
		},
		{
			label: '联系电话',
			name: 'contact_phone',
			placeholder: '请输入联系电话',
		},
		{
			label: '纬度',
			name: 'latitude',
			placeholder: '请输入纬度',
			valueType: EDefaultValueType.Number,
			render: (t: number) => t,
			formItemProps: {
				rules: [{ required: true }],
			},
		},
		{
			label: '经度',
			name: 'longitude',
			placeholder: '请输入经度',
			valueType: EDefaultValueType.Number,
			render: (t: number) => t,
			formItemProps: {
				rules: [{ required: true }],
			},
		},
		{
			label: '配送时间备注',
			name: 'delivery_time_note',
			valueType: EDefaultValueType.Textarea,
			placeholder: '请输入配送时间备注',
		},
	];

	// Handle save distribution point data
	const saveHandle = async () => {
		await form.validateFields();
		const val = form.getFieldsValue(true);
		setLoading(true);
		const fn = distributionPointId
			? updateDistributionPoint(distributionPointId, val)
			: createDistributionPoint(val);
		const res = await fn.finally(() => {
			setLoading(false);
		});
		if (res.success) {
			message.success('保存成功');
			if (!distributionPointId) {
				navigator(transformUrlByRoutePath(PAGES_DISTRIBUTION_POINT_DETAIL_URL, res.data.id));
			}
			setEditable(false);

			fetchDetailData();
		}
	};
	// Render extra buttons
	const renderExtra = () => {
		const editEl = !editable ? (
			<>
				{detailData.enable === EDistributionPointEnable.Disable ? (
					<Button
						onClick={async () => {
							setLoading(true);
							const res = await updateDistributionPointEnable(distributionPointId).finally(() => {
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
							const res = await updateDistributionPointDisable(distributionPointId).finally(() => {
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
				info: distributionPointId
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
								{ label: '状态', name: 'enable', valueEnum: DistributionPointEnableMap },
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

export default DistributionPointDetail;
