import { toValEnumMap } from '@/utils/enum';

export const EMPTY_TEXT = '--';
export const EGlobalBoolMap = toValEnumMap([
	{
		text: '是',
		value: true,
	},
	{
		text: '否',
		value: false,
	},
]);
