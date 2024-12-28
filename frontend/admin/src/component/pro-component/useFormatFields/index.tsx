import { formatRenderFun } from '@/component/pro-component/ProField/render/formatRenderUtil';
import { BaseProFieldType } from '@/component/pro-component/types';

type FormatField<T = any> = Omit<BaseProFieldType<T>, 'readonly' | 'renderFormItem'>;
const useFormatFields = <T = any, U = any>(): {
	formatField: (field: FormatField<U> & Partial<T>) => T;
} => {
	return {
		formatField: field => {
			const ob = formatRenderFun(field, {}) as any;

			return {
				...field,
				render: (t: any, r: any) => {
					return ob.render?.(t, r, -1, { field: field });
				},
			} as any;
		},
	};
};

export default useFormatFields;
