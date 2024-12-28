import React from 'react';
import { ProFormItemsProps } from '@/component/pro-component/ProForm/types';
import ProField from '@/component/pro-component/ProField';
import './index.less';
import { isFunction } from 'lodash';

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
					allEditable={editable}
					{...(it as any)}
					fieldFunc={isFunction(it) ? it : undefined}
				/>
			))}
		</div>
	);
};
export default ProFormItems;
