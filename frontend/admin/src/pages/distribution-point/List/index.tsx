import React, { useRef, useState } from 'react';
import http from '@/utils/http';
import ProTable, {
	ProTableActionType,
	ProTableColumnType,
} from '@/component/pro-component/ProTable';
import { Button, Card, message, Popconfirm, Space } from 'antd';
import ContentLayout from '@/component/ContentLayout';
import {
	PAGES_DISTRIBUTION_POINT_CREATE_URL,
	PAGES_DISTRIBUTION_POINT_DETAIL_URL,
} from '@/pages/distribution-point/pages';
import { EDefaultValueType } from '@yoohoo/pro-component';
import { transformUrlByRoutePath } from '@/utils/url';
import { deleteDistributionPointById } from '@/pages/distribution-point/service';
import { DistributionPointEnableMap } from '@/constant/distribution-point';
import { Link } from 'react-router-dom';
import { QueryFormFieldType } from '@/component/pro-component/ProQueryForm';

const DistributionPointList: React.FC = () => {
	const actionRef = useRef<ProTableActionType>();
	const [loading, setLoading] = useState(false);

	const fetchDistributionPointList = async (params: any) => {
		return await http.get('/admin/distribution-point', {
			params: params,
		});
	};

	const removeHandler = async (id: string) => {
		setLoading(true);
		const res = await deleteDistributionPointById(id).finally(() => {
			setLoading(false);
		});
		if (res.success) {
			message.success('操作成功');
			actionRef.current?.reload();
		}
	};

	const fields: QueryFormFieldType[] = [
		{
			label: '名称',
			name: 'name',
		},
	];

	const columns: ProTableColumnType[] = [
		{
			title: '名称',
			dataIndex: 'name',
			width: 120,
			fixed: 'left',
			ellipsis: true,
		},
		{
			title: '配送点地址',
			dataIndex: 'address',
			width: 250,
			ellipsis: true,
		},
		{
			title: '状态',
			dataIndex: 'enable',
			width: 100,
			valueEnum: DistributionPointEnableMap,
			valueType: EDefaultValueType.EnumStatusDot,
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
						<Link to={transformUrlByRoutePath(PAGES_DISTRIBUTION_POINT_DETAIL_URL, r.id)}>
							详情
						</Link>
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
					request={params => fetchDistributionPointList(params)}
					columns={columns}
					loading={loading}
					fields={fields}
					actionRef={actionRef}
					extraOperation={
						<>
							<Button
								onClick={() => {
									window.open(PAGES_DISTRIBUTION_POINT_CREATE_URL);
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

export default DistributionPointList;
