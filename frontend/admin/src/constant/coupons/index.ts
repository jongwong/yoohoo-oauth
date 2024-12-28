import { toValEnumMap } from '@/utils/enum';

/** 优惠券状态枚举 */
export enum ECouponsStatus {
	Draft = 0, // 草稿中
	PendingApproval = 10, // 审核中
	Rejected = 20, // 审核拒绝
	Approved = 30, // 审核通过
	Expired = 40, // 已过期
}

export const CouponsStatusMap = toValEnumMap<ECouponsStatus>([
	{
		value: ECouponsStatus.Draft,
		text: '草稿中',
		status: 'default',
	},
	{
		value: ECouponsStatus.PendingApproval,
		text: '审核中',
		status: 'warning',
	},
	{
		value: ECouponsStatus.Rejected,
		text: '审核拒绝',
		status: 'error',
	},
	{
		value: ECouponsStatus.Approved,
		text: '审核通过',
		status: 'success',
	},
	{
		value: ECouponsStatus.Expired,
		text: '已过期',
		status: 'default',
	},
]);

/** 优惠券类型枚举 */
export enum ECouponsType {
	Discount = 0, // 折扣券
	Cash = 1, // 现金券
	Percentage = 2, // 百分比折扣券
}

export const CouponsTypeMap = toValEnumMap<ECouponsType>([
	{
		value: ECouponsType.Discount,
		text: '折扣券',
	},
	{
		value: ECouponsType.Cash,
		text: '现金券',
	},
	{
		value: ECouponsType.Percentage,
		text: '百分比折扣券',
	},
]);
