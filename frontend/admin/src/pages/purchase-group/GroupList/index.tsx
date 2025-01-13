import React, { useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import { ContentLayout } from '@yoo/component';
import type {
	ProTableActionType,
	ProTableColumnType,
	ProTableSearchFieldType,
} from '@yoo/pro-component';
import { EDefaultValueType, ProTable } from '@yoo/pro-component';
import { Card, SelectProps, Space } from 'antd';

import { GlobalEnableTypeMap } from '@/constant/common';
import { transformUrlByRoutePath } from '@/utils/url';

import { PAGES_PURCHASE_GROUP_DETAIL_URL } from '../pages'; // 相对路径
import { getPurchaseGroupProductPage } from '../service';
import { PurchaseGroupStatusMap } from '@/constant/purchase-group';
import CategorySelect from '@/component/business/CategorySelect';
import dayjs from 'dayjs'; // 相对路径

const List: React.FC = () => {
	const actionRef = useRef<ProTableActionType>();
	const [loading, setLoading] = useState(false);

	// 获取团购列表
	const fetchPurchaseGroupList = async (params: any) => {
		return await getPurchaseGroupProductPage(params);
	};

	// 查询表单字段
	const fields: ProTableSearchFieldType[] = [
		{
			label: '名称',
			name: 'name',
		},
		{
			label: '商品名称',
			name: 'product_name',
		},
		{
			label: '商品类别',
			name: 'category_id',
			renderFormItem: () => <CategorySelect />,
		},
		{
			label: '团购状态',
			name: 'group_status',
			fieldProps: {
				mode: 'multiple',
			} as SelectProps,
			valueEnum: PurchaseGroupStatusMap,
		},
		{
			label: '启用状态',
			name: 'group_enable',
			valueEnum: GlobalEnableTypeMap,
		},
	];

	// 列配置
	const columns: ProTableColumnType[] = [
		// {
		// 	title: '团购名称',
		// 	dataIndex: 'group_name',
		// 	width: 120,
		// 	fixed: 'left',
		// 	ellipsis: true,
		// },
		{
			title: '商品编号',
			dataIndex: 'code',
			width: 150,
			ellipsis: true,
		},
		{
			title: '商品名称',
			dataIndex: 'name',
			width: 180,
			ellipsis: true,
		},
		{
			title: '商品类别',
			dataIndex: 'price',
			width: 150,
			valueType: EDefaultValueType.Money,
		},
		{
			title: '商品类别',
			dataIndex: 'category_name',
			width: 150,
			ellipsis: true,
		},
		{
			title: '团购状态',
			dataIndex: 'group_status',
			width: 120,
			valueEnum: PurchaseGroupStatusMap,
		},
		{
			title: '团购启用状态',
			dataIndex: 'group_enable',
			width: 120,
			valueEnum: GlobalEnableTypeMap,
		},
		{
			title: '团购时间',
			dataIndex: 'time_group_start',
			width: 250,
			render: (t, r) => {
				return [t, r.time_group_end].map(it => dayjs(it).format('YYYY-MM-DD HH:mm')).join(' ~ ');
			},
		},
		{
			title: '预计到货时间',
			dataIndex: 'time_delivery_start',
			width: 250,
			render: (t, r) => {
				return [t, r.time_delivery_end].map(it => dayjs(it).format('YYYY-MM-DD HH:mm')).join(' ~ ');
			},
		},
		{
			title: '操作',
			dataIndex: '_action',
			width: 120,
			fixed: 'right',
			render: (_t, r) => {
				return (
					<Space>
						<Link
							to={transformUrlByRoutePath(PAGES_PURCHASE_GROUP_DETAIL_URL, r.purchase_group_id)}>
							详情
						</Link>
					</Space>
				);
			},
		},
	];

	return (
		<ContentLayout>
			<Card style={{ minHeight: '100%' }}>
				<ProTable
					scroll={{ x: 'max-content' }}
					request={params => fetchPurchaseGroupList(params)}
					columns={columns}
					loading={loading}
					fields={fields}
					actionRef={actionRef}
				/>
			</Card>
		</ContentLayout>
	);
};

export default List;
