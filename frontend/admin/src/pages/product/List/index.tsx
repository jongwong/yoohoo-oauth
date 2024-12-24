import React from 'react';
import http from '@/utils/http';
import ProTable, { ProTableColumnType } from '@/component/pro-component/ProTable';
import { QueryFormFieldType } from '@/component/pro-component/ProQueryForm';
import { Card, Space, Tag } from 'antd';
import ContentLayout from '@/component/ContentLayout';
import { ProductArchivedStatusMap, ProductListedStatusMap } from '@/constant/product';
import dayjs from 'dayjs';
import { PAGES_PRODUCT_URL } from '@/pages/product/pages';
import { Link } from 'react-router-dom';

const UserList: React.FC = () => {
	const fetchUserList = async (params: any) => {
		return await http.get('/admin/product', {
			params: params,
		}); // 假设返回值中包含 token
	};

	const fields: QueryFormFieldType[] = [
		{
			label: '商品名称',
			name: 'name',
		},
	];

	const columns: ProTableColumnType[] = [
		{
			title: '商品名称',
			dataIndex: 'name',
			width: 200,
			fixed: 'left',
		},
		{
			title: '价格',
			dataIndex: 'price',
			render: (t: number) => `￥${t.toFixed(2)}`,
		},
		{
			title: '建档状态',
			dataIndex: 'archived_status',
			width: 100,
			render: (t: number) => {
				const find = ProductArchivedStatusMap.get(t);

				return find ? <Tag color={find?.status}>{find?.text}</Tag> : '--';
			},
		},
		{
			title: '上架状态',
			dataIndex: 'listed_status',
			width: 100,
			render: (t: number) => {
				const find = ProductListedStatusMap.get(t);
				return find ? <Tag color={find.status}>{find.text}</Tag> : '--';
			},
		},
		{
			title: '创建时间',
			dataIndex: 'created_at',
			render: (t: string) => dayjs(t).format('YYYY-MM-DD HH:mm:ss'),
		},
		{
			title: '创建人',
			dataIndex: 'created_by_name',
		},
		{
			title: '更新时间',
			dataIndex: 'updated_at',
			render: (t: string) => dayjs(t).format('YYYY-MM-DD HH:mm:ss'),
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
						<Link to={PAGES_PRODUCT_URL + '/' + r.id}>详情</Link>
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
					scroll={{ x: 'max-content' }}
					request={params => fetchUserList(params)}
					fields={fields}
					columns={columns}
				/>
			</Card>
		</ContentLayout>
	);
};
export default UserList;
