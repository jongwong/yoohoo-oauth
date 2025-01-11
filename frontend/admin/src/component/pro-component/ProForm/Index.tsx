import React from 'react';

import { Form, FormProps } from 'antd';

import ProFormItem from './ProFormItem';

import ProFormItems from './ProFormItems';

const InternalProForm: <T = any>(props: FormProps<T>) => React.ReactElement = props => {
	const { children, ...rest } = props;

	return <Form {...rest}> {children as any}</Form>;
};

type CompoundedComponent = typeof InternalProForm & {
	/** @experiment 试验性组件 */
	Items: typeof ProFormItems;
	/** @experiment 试验性组件 */
	Item: typeof ProFormItem;
};

const ProFrom = InternalProForm as CompoundedComponent;
ProFrom.Items = ProFormItems;
ProFrom.Item = ProFormItem;
export default ProFrom;
