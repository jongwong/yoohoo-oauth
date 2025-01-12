import { toValEnumMap } from '@/utils/enum';

/** 团购状态（0: 草稿中, 10: 待开团, 20: 开团中, 30: 开团成功, 40: 已结束） */
export enum EPurchaseGroupStatus {
	Draft = 0, // 草稿中
	Waiting = 10, // 待开团
	InProgress = 20, // 开团中
	Success = 30, // 开团成功
	Ended = 40, // 已结束
}

export const PurchaseGroupStatusMap = toValEnumMap<EPurchaseGroupStatus>([
	{
		value: EPurchaseGroupStatus.Draft,
		text: '草稿中',
		status: 'default',
	},
	{
		value: EPurchaseGroupStatus.Waiting,
		text: '待开团',
		status: 'warning',
	},
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
