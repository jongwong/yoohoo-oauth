import React from 'react';
import { SearchSelect, SearchSelectProps } from '@yoo/component';
import http from '@/utils/http';

type UserSearchSelectProps = Omit<SearchSelectProps, 'request'>;
const UserSearchSelect: React.FC<UserSearchSelectProps> = props => {
	const { ...rest } = props;
	return (
		<SearchSelect
			request={getUserPage}
			fieldNames={{
				label: 'name',
				value: 'id',
			}}
			placeholder={'请输入姓名'}
			optionRender={e => {
				return `${e?.data?.name}(${e?.data?.nickname}})`;
			}}
			loadInitialOptions={async e => {
				const res = await getUserBatchAll({ ids: e });
				return res?.data || [];
			}}
			{...rest}
		/>
	);
};
export default UserSearchSelect;

export const getUserPage = (params: Record<string, any>) => {
	return http.get('/admin/user', { params });
};
export const getUserBatchAll = (data: { keys: string[] }) => {
	return http.post('/admin/user/batch', data);
};
