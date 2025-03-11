import React from 'react';
import http from '@/utils/http';
import { SearchSelect, SearchSelectProps } from '@yoo/component';

type DistributionPointSelectProps = Omit<SearchSelectProps, 'request'>;
const DistributionPointSelect: React.FC<DistributionPointSelectProps> = props => {
	const { ...rest } = props;
	return (
		<SearchSelect
			request={getProductPage}
			fieldNames={{
				label: 'name',
				value: 'id',
			}}
			placeholder={'请输入商品名称或者编码'}
			optionRender={e => {
				return e.label;
			}}
			loadInitialOptions={async e => {
				const res = await getProductBatchAll({ ids: e });
				return res?.data || [];
			}}
			{...rest}
		/>
	);
};
export default DistributionPointSelect;

export const getProductPage = (params: Record<string, any>) => {
	return http.get('/admin/distribution-point', { params });
};
export const getProductBatchAll = (data: { keys: string[] }) => {
	return http.post('/admin/distribution-point/batch', data);
};
