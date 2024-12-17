import {toValEnumMap} from "@/utils/enum";

/** 商品建档状态枚举 */
export enum EProductArchivedStatus {
	Draft = 10, // 草稿
	PendingApproval = 20, // 审核中
	ApprovalRejected = 30, // 审核拒绝
	Approved = 40, // 建档完成
}

export const ProductArchivedStatusMap = toValEnumMap<EProductArchivedStatus>([
	{
		value: EProductArchivedStatus.Draft,
		text: '草稿',
		status: 'warning',
	},
	{
		value: EProductArchivedStatus.PendingApproval,
		text: '审核中',
		status: 'processing',
	},
	{
		value: EProductArchivedStatus.ApprovalRejected,
		text: '审核拒绝',
		status: 'error',
	},
	{
		value: EProductArchivedStatus.Approved,
		text: '建档完成',
		status: 'success',
	},
]);

/** 商品上架状态枚举 */
export enum EProductListedStatus {
	Unlisted = 0, // 未上架
	Listed = 1, // 已上架
}

export const ProductListedStatusMap = toValEnumMap<EProductListedStatus>([
	{
		value: EProductListedStatus.Unlisted,
		text: '未上架',
		status: 'default',
	},
	{
		value: EProductListedStatus.Listed,
		text: '已上架',
		status: 'success',
	},
]);
