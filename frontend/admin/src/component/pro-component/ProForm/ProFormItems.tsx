import React from 'react';

import { isFunction } from 'lodash';

import ProField from '../ProField';
import { ProFormItemsProps } from '@yoo/pro-component';

import './index.less';

const ProFormItems: React.FC<ProFormItemsProps> = props => {
	const { fields = [], editable = false, ...rest } = props;
	return (
		<div
			className={
				!editable ? 'yh-pro-form yh-pro-form-readonly' : 'yh-pro-form yh-pro-form-editing'
			}>
			{fields?.map((it: any) => (
				<ProField
					key={it?.name || (it as any)?.key}
					_needLayout
					allEditable={editable}
					{...(it as any)}
					fieldFunc={isFunction(it) ? it : undefined}
					{...rest}
				/>
			))}
		</div>
	);
};
export default ProFormItems;
