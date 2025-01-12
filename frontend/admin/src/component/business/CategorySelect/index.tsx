import React from 'react';

import { SearchSelect, SearchSelectProps } from '@yoo/component';

import http from '@/utils/http';

type DataItem = {
	code: string;
	id: string;
	name: string;
};
type CategorySelectProps<T = any> = Omit<SearchSelectProps<T>, 'request'>;
const CategorySearchSelect: React.FC<CategorySelectProps<DataItem>> = props => {
	const { ...rest } = props;
	return (
		<SearchSelect<DataItem>
			request={getDataPage}
			fieldNames={{
				label: 'name',
				value: 'id',
			}}
			placeholder={'请输入名称或者编码'}
			optionRender={e => {
				return (e?.data?.code || '--') + ':' + e.label;
			}}
			loadInitialOptions={async e => {
				const res = await getDataBatchAll({ ids: e });
				return res?.data || [];
			}}
			{...(rest as any)}
		/>
	);
};
export default CategorySearchSelect;

export const getDataPage = (params: Record<string, any>) => {
	return http.get('/admin/product-category', { params });
};
export const getDataBatchAll = (data: { keys: string[] }) => {
	return http.post('/admin/product-category/batch', data);
};
