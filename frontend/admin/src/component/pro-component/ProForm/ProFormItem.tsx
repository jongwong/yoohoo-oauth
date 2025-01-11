import React from 'react';

import { isFunction } from 'lodash';

import ProField from '@/component/pro-component/ProField';

import './index.less';

import { BaseFormProFieldFuncType } from '../types';

const ProFormItem: React.FC<
	BaseFormProFieldFuncType & {
		allEditable: boolean;
	}
> = props => {
	return (
		<>
			<ProField
				key={props?.name || (props as any)?.key}
				allEditable={props?.allEditable}
				{...(props as any)}
				fieldFunc={isFunction(props) ? props : undefined}
			/>
		</>
	);
};
export default ProFormItem;
