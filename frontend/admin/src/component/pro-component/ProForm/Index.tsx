import React from 'react';
import ProFormItems from './ProFormItems';
import { Form, FormProps } from 'antd';

const InternalProForm: <T = any>(props: FormProps<T>) => React.ReactElement = props => {
	const { children, ...rest } = props;

	return <Form {...rest}> {children as any}</Form>;
};

type CompoundedComponent = typeof InternalProForm & {
	/** @experiment 试验性组件 */
	Items: typeof ProFormItems;
};

const ProFrom = InternalProForm as CompoundedComponent;
ProFrom.Items = ProFormItems;
export default ProFrom;
