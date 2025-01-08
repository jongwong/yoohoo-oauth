import { toValEnumMap } from '@/utils/enum';

/** 是否弃用（1：启用，0：停用） */
export enum EDistributionPointEnable {
	Disable = 0,
	Enable = 1,
}

export const DistributionPointEnableMap = toValEnumMap<EDistributionPointEnable>([
	{
		value: EDistributionPointEnable.Disable,
		text: '停用',
		status: 'default',
	},
	{
		value: EDistributionPointEnable.Enable,
		text: '启用',
		status: 'success',
	},
]);
