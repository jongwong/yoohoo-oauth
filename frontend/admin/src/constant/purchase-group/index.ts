import { toValEnumMap } from '@/utils/enum';

/** 团购状态（1：开团中，2：开团成功，3：已结束） */
export enum EPurchaseGroupStatus {
	InProgress = 1, // 开团中
	Success = 2, // 开团成功
	Ended = 3, // 已结束
}

export const PurchaseGroupStatusMap = toValEnumMap<EPurchaseGroupStatus>([
	{
		value: EPurchaseGroupStatus.InProgress,
		text: '开团中',
		status: 'processing',
	},
	{
		value: EPurchaseGroupStatus.Success,
		text: '开团成功',
		status: 'success',
	},
	{
		value: EPurchaseGroupStatus.Ended,
		text: '已结束',
		status: 'default',
	},
]);
