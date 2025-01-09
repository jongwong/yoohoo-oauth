import React, { useRef, useState } from 'react';
import type { ProTableSearchFieldType } from '@yoo/pro-component';
import {
	EDefaultValueType,
	ProTable,
	ProTableActionType,
	ProTableColumnType,
} from '@yoo/pro-component';
import { Button, Card, message, Popconfirm, Space } from 'antd';
import { ContentLayout } from '@yoo/component';
import {
	PAGES_PRODUCT_CATEGORY_CREATE_URL,
	PAGES_PRODUCT_CATEGORY_DETAIL_URL,
} from '@/pages/product-category/pages';
import { transformUrlByRoutePath } from '@/utils/url';
import {
	deleteProductCategoryById,
	getProductCategoryPage,
} from '@/pages/product-category/service';
import { Link } from 'react-router-dom';
import { CategoryLevelMap } from '@/constant/product_category';
import { GlobalEnableTypeMap } from '@/constant/common';

const List: React.FC = () => {
	const actionRef = useRef<ProTableActionType>();
	const [loading, setLoading] = useState(false);

	// 修改为获取商品类别列表
	const fetchProductCategoryList = async (params: any) => {
		return await getProductCategoryPage(params);
	};

	// 删除类别的操作
	const removeHandler = async (id: string) => {
		setLoading(true);
		const res = await deleteProductCategoryById(id).finally(() => {
			setLoading(false);
		});
		if (res.success) {
			message.success('操作成功');
			actionRef.current?.reload();
		}
	};

	// 修改查询表单字段
	const fields: ProTableSearchFieldType[] = [
		{
			label: '名称',
			name: 'name',
		},
	];

	// 修改列配置
	const columns: ProTableColumnType[] = [
		{
			title: '类别名称',
			dataIndex: 'name',
			width: 120,
			fixed: 'left',
			ellipsis: true,
		},
		{
			title: '类别代码',
			dataIndex: 'code',
			width: 150,
			ellipsis: true,
		},
		{
			title: '层级',
			dataIndex: 'level',
			width: 100,
			valueEnum: CategoryLevelMap,
		},
		{
			title: '是否开启',
			dataIndex: 'enable',
			width: 100,
			valueEnum: GlobalEnableTypeMap,
			valueType: EDefaultValueType.EnumStatusTag,
		},

		{
			title: '父级类别',
			dataIndex: 'parent_name',
			width: 150,
			ellipsis: true,
		},

		{
			title: '类别描述',
			dataIndex: 'description',
			width: 250,
			ellipsis: true,
		},
		{
			title: '创建人',
			dataIndex: 'created_by_name',
		},
		{
			title: '创建时间',
			dataIndex: 'created_at',
			valueType: EDefaultValueType.DateTimeMinutes,
		},
		{
			title: '更新人',
			dataIndex: 'updated_by_name',
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
						<Link to={transformUrlByRoutePath(PAGES_PRODUCT_CATEGORY_DETAIL_URL, r.id)}>详情</Link>
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
			<Card
				style={{
					minHeight: '100%',
				}}>
				<ProTable
					scroll={{ x: 'max-content' }}
					request={params => fetchProductCategoryList(params)}
					columns={columns}
					loading={loading}
					fields={fields}
					actionRef={actionRef}
					extraOperation={
						<>
							<Button
								onClick={() => {
									window.open(PAGES_PRODUCT_CATEGORY_CREATE_URL);
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
