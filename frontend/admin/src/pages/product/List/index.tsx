import React, { useRef, useState } from 'react';
import { Link } from 'react-router-dom';

import { ContentLayout } from '@yoo/component';
import type { ProTableSearchFieldType } from '@yoo/pro-component';
import {
	EDefaultValueType,
	ProTable,
	ProTableActionType,
	ProTableColumnType,
} from '@yoo/pro-component';
import { Button, Card, message, Popconfirm, Space } from 'antd';

import {
	EProductArchivedStatus,
	ProductArchivedStatusMap,
	ProductListedStatusMap,
} from '@/constant/product';
import { PAGES_PRODUCT_CREATE_URL, PAGES_PRODUCT_DETAIL_URL } from '@/pages/product/pages';
import { deleteProductById } from '@/pages/product/service';
import http from '@/utils/http';
import { transformUrlByRoutePath } from '@/utils/url';

const UserList: React.FC = () => {
	const actionRef = useRef<ProTableActionType>();
	const [loading, setLoading] = useState(false);

	const fetchUserList = async (params: any) => {
		return await http.get('/admin/product', {
			params: params,
		}); // 假设返回值中包含 token
	};

	const fields: ProTableSearchFieldType[] = [
		{
			label: '商品名称',
			name: 'name',
		},
	];
	const removeHandler = async (id: string) => {
		setLoading(true);
		const res = await deleteProductById(id).finally(() => {
			setLoading(false);
		});
		if (res.success) {
			message.success('操作成功');
			actionRef.current?.reload();
		}
	};

	const columns: ProTableColumnType[] = [
		{
			title: '商品名称',
			dataIndex: 'name',
			width: 120,
			fixed: 'left',
			ellipsis: true,
		},
		{
			title: '价格',
			dataIndex: 'price',
			valueType: EDefaultValueType.Money,
		},
		{
			title: '建档状态',
			dataIndex: 'archived_status',
			width: 100,
			valueEnum: ProductArchivedStatusMap,
			valueType: EDefaultValueType.EnumStatusDot,
		},
		{
			title: '上架状态',
			dataIndex: 'listed_status',
			width: 100,
			valueEnum: ProductListedStatusMap,
			valueType: EDefaultValueType.EnumStatusDot,
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
						<Link to={transformUrlByRoutePath(PAGES_PRODUCT_DETAIL_URL, r.id)}>详情</Link>
						{r?.archived_status === EProductArchivedStatus.Draft ||
						r?.archived_status === EProductArchivedStatus.Rejected ? (
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
					scroll={{ x: 'max-content' }}
					request={params => fetchUserList(params)}
					fields={fields}
					columns={columns}
					footer={{
						name: 'archived_status',
						items: ProductArchivedStatusMap.tabs({ addUnLimit: true, limitText: '全部' }),
					}}
					loading={loading}
					actionRef={actionRef}
					extraOperation={
						<>
							<Button
								onClick={() => {
									window.open(PAGES_PRODUCT_CREATE_URL);
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
