import React, { useRef, useState } from 'react';
import { Link } from 'react-router-dom';

import type {
	ProTableActionType,
	ProTableColumnType,
	QueryFormFieldType,
} from '@yoo/pro-component';
import { EDefaultValueType, ProTable } from '@yoo/pro-component';
import { Button, Card, message, Popconfirm, Space } from 'antd';

import { ContentLayout } from '@yoo/component';
import { GlobalEnableTypeMap } from '@/constant/common';
import { transformUrlByRoutePath } from '@/utils/url';

import { PAGES_PURCHASE_GROUP_CREATE_URL, PAGES_PURCHASE_GROUP_DETAIL_URL } from '../pages'; // 相对路径
import { deletePurchaseGroupById, getPurchaseGroupPage } from '../service'; // 相对路径

const List: React.FC = () => {
	const actionRef = useRef<ProTableActionType>();
	const [loading, setLoading] = useState(false);

	// 获取团购列表
	const fetchPurchaseGroupList = async (params: any) => {
		return await getPurchaseGroupPage(params);
	};

	// 删除团购的操作
	const removeHandler = async (id: string) => {
		setLoading(true);
		const res = await deletePurchaseGroupById(id).finally(() => {
			setLoading(false);
		});
		if (res.success) {
			message.success('操作成功');
			actionRef.current?.reload();
		}
	};

	// 查询表单字段
	const fields: QueryFormFieldType[] = [
		{
			label: '名称',
			name: 'name',
		},
	];

	// 列配置
	const columns: ProTableColumnType[] = [
		{
			title: '名称',
			dataIndex: 'name',
			width: 120,
			fixed: 'left',
			ellipsis: true,
		},
		{
			title: '编码',
			dataIndex: 'code',
			width: 150,
			ellipsis: true,
		},
		{
			title: '是否启用',
			dataIndex: 'enable',
			width: 100,
			valueEnum: GlobalEnableTypeMap,
			valueType: EDefaultValueType.EnumStatusTag,
		},
		{
			title: '创建人',
			dataIndex: 'created_by_name',
			width: 120,
		},
		{
			title: '创建时间',
			dataIndex: 'created_at',
			valueType: EDefaultValueType.DateTimeMinutes,
		},
		{
			title: '更新人',
			dataIndex: 'updated_by_name',
			width: 120,
		},
		{
			title: '更新时间',
			dataIndex: 'updated_at',
			valueType: EDefaultValueType.DateTimeMinutes,
		},
		{
			title: '操作',
			dataIndex: '_action',
			width: 120,
			fixed: 'right',
			render: (_t, r) => {
				return (
					<Space>
						<Link to={transformUrlByRoutePath(PAGES_PURCHASE_GROUP_DETAIL_URL, r.id)}>详情</Link>
						<Popconfirm
							title="确认是否删除？"
							onConfirm={() => {
								removeHandler(r?.id);
							}}>
							<a>删除</a>
						</Popconfirm>
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
					extraOperation={
						<>
							<Button
								onClick={() => {
									window.open(PAGES_PURCHASE_GROUP_CREATE_URL);
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

export default List;
