import React from 'react';
import { ProFormItemsProps } from '@/component/pro-component/ProForm/types';
import { formatRenderFun } from '@/component/pro-component/ProField/render/formatRenderUtil';
import ProField from '@/component/pro-component/ProField';

const ProFormItems: React.FC<ProFormItemsProps> = props => {
	const { fields, fieldsDeps = [], readonly, ...rest } = props;
	const getTransformFields = () => {
		return fields?.map(it => ({
			...it,
			...formatRenderFun(it, {}),
		}));
	};

	const formatFields = getTransformFields();
	return (
		<div>
			{formatFields?.map(it => (
				<ProField {...(it as any)} readonly={readonly || it.readonly} />
			))}
		</div>
	);
};
export default ProFormItems;
