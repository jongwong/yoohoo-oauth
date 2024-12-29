import React from 'react';
import { EDefaultValueType, ProTable } from '@yoohoo/pro-component';
import { ProTableColumnType } from '@/component/pro-component/ProTable';
import http from '@/utils/http';
import { Card } from 'antd';

type UserCouponsTableProps = {
	couponsId?: string;
};
const UserCouponsTable: React.FC<UserCouponsTableProps> = props => {
	const { couponsId, ...rest } = props;
	const columns: ProTableColumnType[] = [
		{
			title: '发放时间',
			dataIndex: 'created_at',
			valueType: EDefaultValueType.DateTimeMinutes,
		},
		{
			title: '关联用户',
			dataIndex: 'user_name',
		},
		{
			title: '是否已使用',
			dataIndex: 'is_used',
			render: (t: boolean) => (t ? '已使用' : '未使用'),
		},
		{
			title: '使用时间',
			dataIndex: 'used_at',
			valueType: EDefaultValueType.DateTimeMinutes,
		},
		{
			title: '有效期开始时间',
			dataIndex: 'valid_from',
			valueType: EDefaultValueType.DateTimeMinutes,
		},
		{
			title: '有效期结束时间',
			dataIndex: 'valid_to',
			valueType: EDefaultValueType.DateTimeMinutes,
		},
		{
			title: '动态适用范围类型',
			dataIndex: 'dynamic_scope_type',
			render: (scopeType: number) => {
				switch (scopeType) {
					case 0:
						return '分类';
					case 1:
						return '商品';
					case 2:
						return '品牌';
					default:
						return '无';
				}
			},
		},
		{
			title: '关联id',
			dataIndex: 'dynamic_scope_id',
		},
	];
	return couponsId ? (
		<Card>
			<ProTable
				columns={columns}
				request={params => {
					return http.get('/admin/user-coupons/by-coupon/' + couponsId, {
						params: params,
					});
				}}
			/>
		</Card>
	) : null;
};
export default UserCouponsTable;
