import React, { useRef, useState } from 'react';
import ProTable, {
	ProTableActionType,
	ProTableColumnType,
} from '@/component/pro-component/ProTable';
import { QueryFormFieldType } from '@/component/pro-component/ProQueryForm';
import { Button, Card, message, Popconfirm, Space } from 'antd';
import ContentLayout from '@/component/ContentLayout';
import dayjs from 'dayjs';
import { Link } from 'react-router-dom';
import { CouponsStatusMap, CouponsTypeMap, ECouponsStatus } from '@/constant/coupons';
import { EDefaultValueType } from '@yoohoo/pro-component';
import { transformUrlByRoutePath } from '@/utils/url';
import { PAGES_COUPONS_CREATE_URL, PAGES_COUPONS_DETAIL_URL } from '@/pages/coupons/pages';
import { deleteCouponsById, getCouponsPage } from '@/pages/coupons/service';

const UserList: React.FC = () => {
	const fetchList = async (params: any) => {
		return await getCouponsPage(params); // 假设返回值中包含 token
	};
	const [loading, setLoading] = useState(false);

	const actionRef = useRef<ProTableActionType>();
	const fields: QueryFormFieldType[] = [
		{
			label: '名称',
			name: 'name',
		},
	];
	const removeHandler = async (id: string) => {
		setLoading(true);
		const res = await deleteCouponsById(id).finally(() => {
			setLoading(false);
		});
		if (res.success) {
			message.success('操作成功');
			actionRef.current?.reload();
		}
	};

	const columns: ProTableColumnType[] = [
		{
			title: '名称',
			dataIndex: 'name',
			width: 200,
			fixed: 'left',
		},
		{
			title: '优惠券类型',
			dataIndex: 'type',
			valueEnum: CouponsTypeMap,
		},
		{
			title: '折扣金额',
			dataIndex: 'discount_amount',
			valueType: EDefaultValueType.Money,
		},
		{
			title: '折扣百分比',
			dataIndex: 'discount_percentage',
			valueType: EDefaultValueType.Percentage,
		},
		{
			title: '最低消费金额',
			dataIndex: 'min_spend',
			valueType: EDefaultValueType.Money,
		},
		{
			title: '最大折扣',
			dataIndex: 'max_discount',
			valueType: EDefaultValueType.Integer,
		},
		{
			title: '有效期',
			dataIndex: 'valid_from',
			render: (t: string, r) => {
				const validFrom = dayjs(r.valid_from).format('YYYY-MM-DD');
				const validTo = dayjs(r.valid_to).format('YYYY-MM-DD');
				return `${validFrom} 至 ${validTo}`;
			},
		},
		{
			title: '发放总量',
			dataIndex: 'total_issued',
			valueType: EDefaultValueType.Integer,
		},
		{
			title: '已领取数量',
			dataIndex: 'total_claimed',
			valueType: EDefaultValueType.Integer,
		},
		{
			title: '创建时间',
			dataIndex: 'created_at',
			valueType: EDefaultValueType.DateTimeMinutes,
		},
		{
			title: '创建人',
			dataIndex: 'created_by_name',
		},
		{
			title: '更新时间',
			dataIndex: 'updated_at',
			valueType: EDefaultValueType.DateTimeMinutes,
		},
		{
			title: '更新人',
			dataIndex: 'updated_by_name',
		},
		{
			title: '操作',
			dataIndex: '_action',
			width: 120,
			fixed: 'right',
			render: (_t, r) => {
				return (
					<Space>
						<Link to={transformUrlByRoutePath(PAGES_COUPONS_DETAIL_URL, r.id)}>详情</Link>

						{r?.status === ECouponsStatus.Draft || r?.status === ECouponsStatus.Rejected ? (
							<Popconfirm
								title="确认是否删除？"
								onConfirm={() => {
									removeHandler(r?.id);
								}}>
								<a>删除</a>
							</Popconfirm>
						) : null}
					</Space>
				);
			},
		},
	];

	return (
		<ContentLayout>
			<Card
				style={{
					minHeight: '100%',
				}}>
				<ProTable
					footer={{
						name: 'status',
						items: CouponsStatusMap.tabs({ addUnLimit: true, limitText: '全部' }),
					}}
					scroll={{ x: 'max-content' }}
					request={params => fetchList(params)}
					fields={fields}
					loading={loading}
					columns={columns}
					actionRef={actionRef}
					extraOperation={
						<>
							<Button
								onClick={() => {
									window.open(PAGES_COUPONS_CREATE_URL);
								}}>
								新建
							</Button>
						</>
					}
				/>
			</Card>
		</ContentLayout>
	);
};
export default UserList;
