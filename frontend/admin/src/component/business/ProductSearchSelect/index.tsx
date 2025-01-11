import React from 'react';
import { SearchSelect, SearchSelectProps } from '@yoo/component';
import http from '@/utils/http';

type ProductSearchSelectProps = Omit<SearchSelectProps, 'request'>;
const ProductSearchSelect: React.FC<ProductSearchSelectProps> = props => {
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
				console.log('=====e=====', e);
				return (e?.data?.code || '--') + ':' + e.label;
			}}
			loadInitialOptions={async e => {
				const res = await getProductBatchAll({ ids: e });
				return res?.data || [];
			}}
			{...rest}
		/>
	);
};
export default ProductSearchSelect;

export const getProductPage = (params: Record<string, any>) => {
	return http.get('/admin/product', { params });
};
export const getProductBatchAll = (data: { keys: string[] }) => {
	return http.post('/admin/product/batch', data);
};
