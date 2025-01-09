import React from 'react';
import SearchSelect from '@/component/SearchSelect';

type ProductSearchSelectProps = {};
const ProductSearchSelect: React.FC<ProductSearchSelectProps> = props => {
	const { ...rest } = props;
	return <SearchSelect />;
};
export default ProductSearchSelect;
