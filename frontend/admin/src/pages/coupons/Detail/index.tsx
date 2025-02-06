import React, { useEffect, useRef, useState } from 'react';
import http from '@/utils/http';
import { useParams } from 'react-router-dom';
import { ContentLayout } from '@yoo/component';
import { EDefaultValueType, ProForm, ProFormItemsFieldType } from '@yoo/pro-component';
import { Button, Card, Form, message, Space } from 'antd';
import { useUpdate } from 'ahooks';
import { CouponsStatusMap, CouponsTypeMap, ECouponsStatus, ECouponsType } from '@/constant/coupons';
import { transformUrlByRoutePath } from '@/utils/url';
import { PAGES_COUPONS_DETAIL_URL } from '@/pages/coupons/pages';
import UserCouponsTable from './UserCouponsTable';

const UserDetail: React.FC = props => {
	const params = useParams();
	const { couponsId } = params;
	const [form] = Form.useForm();
	const forceUpdate = useUpdate();
	const [detailData, setDetailData] = useState<Record<string, any>>({});
	const [loading, setLoading] = useState(false);
	const historyEditDataRef = useRef();
	const [editable, setEditable] = useState(false);

	const fetchDetailData = async () => {
		if (!couponsId) {
			setDetailData({
				status: ECouponsStatus.Draft,
			});
			setEditable(true);
			return;
		}
		setLoading(true);
		const res = await http.get('/admin/coupons/' + couponsId).finally(() => {
			setLoading(false);
		});
		form.setFieldsValue({
			...res?.data,
		});
		setDetailData({ ...res?.data });
		forceUpdate();
		return res; // 假设返回值中包含 token
	};

	const fields: ProFormItemsFieldType[] = [
		{ label: '优惠券名称', name: 'name', placeholder: '请输入优惠券名称' },
		{
			label: '优惠券类型',
			name: 'type',
			valueEnum: CouponsTypeMap, // 假设你有 CouponTypeMap 来映射优惠券类型
			placeholder: '请选择优惠券类型',
		},
		r => ({
			label: '折扣金额',
			name: 'discount_amount',
			valueType: EDefaultValueType.Money,
			placeholder: '请输入折扣金额',
			formItemProps: {
				rules: [{ required: true, message: '折扣金额不能为空' }],
			},
			visible: r.type === ECouponsType.Discount,
		}),
		r => ({
			label: '折扣百分比',
			name: 'discount_percentage',
			valueType: EDefaultValueType.Percentage,
			placeholder: '请输入折扣百分比',
			visible: r.type === ECouponsType.Percentage,
		}),
		r => ({
			label: '最低消费金额',
			name: 'min_spend',
			valueType: EDefaultValueType.Money,
			placeholder: '请输入最低消费金额',
			visible: r.type === ECouponsType.Cash,
		}),

		{
			label: '使用开始时间',
			name: 'valid_from',
			valueType: EDefaultValueType.DateTime,
			placeholder: '请选择优惠券开始时间',
		},
		{
			label: '使用结束时间',
			name: 'valid_to',
			valueType: EDefaultValueType.DateTime,
			placeholder: '请选择优惠券结束时间',
		},
		{
			label: '发放总量',
			name: 'total_issued',
			valueType: EDefaultValueType.PositiveInteger,
			placeholder: '请输入优惠券总发放量',
		},
		{
			label: '已使用数量',
			name: 'total_used',
			valueType: 'digit',
			placeholder: '请输入已使用数量',
			editable: false,
			visible: !editable,
		},
		{
			label: '已领取数量',
			name: 'total_claimed',
			valueType: EDefaultValueType.Integer,
			placeholder: '请输入已领取数量',
			editable: false,
			visible: !editable,
		},
	];

	const saveHandle = async () => {
		await form.validateFields();
		const val = form.getFieldsValue(true);
		setLoading(true);
		const fn = !couponsId
			? http.post('/admin/coupons', val)
			: http.put('/admin/coupons/' + couponsId, val);
		const res = await fn.finally(() => {
			setLoading(false);
		});
		if (res.success) {
			message.success('保存成功');
			if (!couponsId) {
				window.open(transformUrlByRoutePath(PAGES_COUPONS_DETAIL_URL, res.data.id));
				return;
			} else {
				fetchDetailData();
			}
		}
	};

	useEffect(() => {
		fetchDetailData();
	}, []);
	const submitHandle = async () => {
		await form.validateFields();
		const val = form.getFieldsValue(true);
		setLoading(true);
		const res = await http.put('/admin/coupons/' + couponsId + '/submit', val).finally(() => {
			setLoading(false);
		});
		if (res.success) {
			message.success('提交成功');
			if (!couponsId) {
				window.open(transformUrlByRoutePath(PAGES_COUPONS_DETAIL_URL, res.data.id));
				return;
			}
			if (editable) {
				setEditable(false);
			}
			fetchDetailData();
		}
	};
	const rejectHandle = async () => {
		setLoading(true);
		const res = await http
			.put('/admin/coupons/' + couponsId + '/reject', {
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
		const res = await http.put('/admin/coupons/' + couponsId + '/approve').finally(() => {
			setLoading(false);
		});
		if (res.success) {
			message.success('操作成功');
			fetchDetailData();
		}
	};

	const renderExtra = () => {
		const editEl = !editable ? (
			<Button
				onClick={() => {
					historyEditDataRef.current = form.getFieldsValue(true);
					setEditable(true);
				}}>
				编辑
			</Button>
		) : (
			<>
				<Button
					onClick={() => {
						form.setFieldsValue(historyEditDataRef.current);
						setEditable(false);
					}}>
					取消
				</Button>
				{detailData?.status <= ECouponsStatus.Draft ? (
					<Button onClick={() => saveHandle()}>保存草稿</Button>
				) : null}
			</>
		);
		return (
			<Space>
				{detailData?.status <= ECouponsStatus.Draft ||
				detailData?.status === ECouponsStatus.Rejected
					? editEl
					: null}

				{couponsId && detailData?.status <= ECouponsStatus.Draft ? (
					<Button type={'primary'} onClick={() => submitHandle()}>
						提交审核
					</Button>
				) : null}

				{detailData?.status === ECouponsStatus.PendingApproval ? (
					<Button type={'primary'} danger onClick={() => rejectHandle()}>
						审核拒绝
					</Button>
				) : null}
				{detailData?.status === ECouponsStatus.PendingApproval ? (
					<Button type={'primary'} onClick={() => approvalHandle()}>
						审核通过
					</Button>
				) : null}

				{detailData?.status === ECouponsStatus.Rejected ? (
					<Button type={'primary'} onClick={() => submitHandle()}>
						重新提交审核
					</Button>
				) : null}
			</Space>
		);
	};
	const renderBaseInfo = () => {
		return (
			<Card>
				<Form
					form={form}
					labelCol={{
						span: 4,
					}}
					wrapperCol={{
						span: 7,
					}}>
					<ProForm.Items editable={editable} fields={fields} />
				</Form>
			</Card>
		);
	};

	return (
		<ContentLayout
			loading={loading}
			header={{
				extra: renderExtra(),
				info: {
					data: detailData,
					leftItems: [
						{
							label: '优惠券名称',
							name: 'name',
						},
						{
							label: '优惠券类型',
							name: 'type',
							valueEnum: CouponsTypeMap,
						},
						{
							label: '创建时间',
							name: 'created_at',
							valueType: EDefaultValueType.DateTime,
						},
						{
							label: '更新时间',
							name: 'updated_at',
							valueType: EDefaultValueType.DateTime,
						},
						{
							label: '创建人',
							name: 'created_by_name',
						},
						{
							label: '更新人',
							name: 'updated_by_name',
						},
					],
					rightItems: [
						{
							label: '状态',
							name: 'status',
							valueEnum: CouponsStatusMap,
							valueType: EDefaultValueType.EnumStatusText,
						},
					],
				},
				tabsProps:
					detailData?.status >= ECouponsStatus.Approved
						? {
								items: [
									{
										label: '基础信息',
										key: 'baseInfo',
										children: renderBaseInfo(),
									},
									{
										label: '用户优惠券',
										key: 'userCoupons',
										children: <UserCouponsTable couponsId={couponsId} />,
									},
								],
						  }
						: undefined,
			}}>
			{detailData?.status < ECouponsStatus.Approved ? renderBaseInfo() : null}

			{/*{userCouponsVisible && <UserCouponsTable />}*/}
		</ContentLayout>
	);
};
export default UserDetail;
