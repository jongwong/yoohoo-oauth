import React from 'react';
import http from '@/utils/http';
import ProTable, { ProTableColumnType } from '@/component/pro-component/ProTable';
import { QueryFormFieldType } from '@/component/pro-component/ProQueryForm';
import { Card } from 'antd';
import ContentLayout from '@/component/ContentLayout';
import { EDefaultValueType } from '@yoohoo/pro-component';

const UserList: React.FC = props => {
	const fetchUserList = async (params: any) => {
		return await http.get('/admin/user', {
			params: params,
		}); // 假设返回值中包含 token
	};

	const fields: QueryFormFieldType[] = [
		{
			label: '用户名',
			name: 'name',
		},
		{
			label: '手机号',
			name: 'mobile',
		},
	];
	const columns: ProTableColumnType[] = [
		{
			title: '用户名',
			dataIndex: 'name',
			fixed: 'left',
			width: 100,
			ellipsis: true,
		},

		{
			title: '昵称',
			dataIndex: 'nickname',
			width: 100,
			ellipsis: true,
		},

		{
			title: '手机号',
			dataIndex: 'mobile',
			width: 120,
		},
		{
			title: '用户名',
			dataIndex: 'username',
			width: 100,
			ellipsis: true,
		},
		{
			title: '邮箱',
			dataIndex: 'email',
			width: 100,
			ellipsis: true,
		},
		{
			title: '状态',
			dataIndex: 'enabled',
			render: t => (t ? '启用' : '禁用'),
			width: 80,
		},
		{
			title: '权限',
			dataIndex: 'authorities',
			render: t => (t?.length > 0 ? t?.join(', ') : undefined),
			width: 150,
			ellipsis: true,
		},
		{
			title: '最后登录时间',
			dataIndex: 'last_login_at',
			valueType: EDefaultValueType.DateTime,
			width: 170,
		},
		{
			title: '创建时间',
			dataIndex: 'created_at',
			valueType: EDefaultValueType.DateTimeMinutes,
			width: 150,
		},
		{
			title: '创建时间',
			dataIndex: 'updated_at',
			valueType: EDefaultValueType.DateTimeMinutes,
			width: 150,
		},
	];
	return (
		<ContentLayout>
			<Card
				style={{
					minHeight: '100%',
				}}>
				<ProTable
					scroll={{
						x: 'max-content',
					}}
					request={params => fetchUserList(params)}
					fields={fields}
					columns={columns}
				/>
			</Card>
		</ContentLayout>
	);
};
export default UserList;
