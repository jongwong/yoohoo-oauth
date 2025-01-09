import { toValEnumMap } from '@/utils/enum';

/** 是否弃用（1：启用，0：停用） */
export enum EGlobalEnableType {
	Disable = 0,
	Enable = 1,
}

export const GlobalEnableTypeMap = toValEnumMap<EGlobalEnableType>([
	{
		value: EGlobalEnableType.Disable,
		text: '停用',
		status: 'default',
	},
	{
		value: EGlobalEnableType.Enable,
		text: '启用',
		status: 'success',
	},
]);
