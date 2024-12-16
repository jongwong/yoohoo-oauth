import React from 'react';
import http from '@/utils/http';
import ProTable, { ProTableColumnType } from '@/component/pro-component/ProTable';
import { QueryFormFieldType } from '@/component/pro-component/ProQueryForm';
import { Card } from 'antd';
import ContentLayout from '@/component/ContentLayout';

const UserList: React.FC = props => {
	const fetchUserList = async (params: any) => {
		const response = await http.get('/admin/user', {
			params: params,
		});
		return response.data; // 假设返回值中包含 token
	};

	const fields: QueryFormFieldType[] = [
		{
			label: '用户ID',
			name: 'username',
		},
		{
			label: '昵称',
			name: 'nickname',
		},
	];

	const columns: ProTableColumnType[] = [
		{
			title: '用户ID',
			dataIndex: 'username',
		},
		{
			title: '昵称',
			dataIndex: 'nickname',
		},
	];
	return (
		<ContentLayout>
			<Card
				style={{
					minHeight: '100%',
				}}>
				<ProTable request={params => fetchUserList(params)} fields={fields} columns={columns} />
			</Card>
		</ContentLayout>
	);
};
export default UserList;
