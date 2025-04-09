import { formatRenderFun } from '../ProField/render/formatRenderUtil';
import { BaseProFieldType } from '../types';

type FormatField<T = any> = Omit<BaseProFieldType<T>, 'readonly' | 'renderFormItem'>;
const useFormatFields = <T = any, U = any>(): {
	formatField: (field: FormatField<U> & Partial<T>) => T;
} => {
	return {
		formatField: field => {
			const ob = formatRenderFun(field, {}) as any;

			return {
				...field,
				render: (t: any, r: any, idx?: number) => {
					return ob.render?.(t, r, idx, { field: field });
				},
			} as any;
		},
	};
};

export default useFormatFields;
