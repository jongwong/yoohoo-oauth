import { toValEnumMap } from '@/utils/enum';

export enum EOrderStatus {
	PendingPayment = 10, // 待支付
	PendingDelivery = 20, // 待配送
	Preparing = 30, // 备餐中
	InDelivery = 40, // 配送中
	Completed = 50, // 已完成
	Cancelled = 60, // 已取消
	RefundInProgress = 70, // 退款中
	Refunded = 80, // 已退款
	RefundFailed = 90, // 退款失败
}

export const EOrderStatusMap = toValEnumMap([
	{
		value: EOrderStatus.PendingPayment,
		text: '待支付',
		status: 'default',
	},
	{
		value: EOrderStatus.PendingDelivery,
		text: '待收货',
		status: 'processing',
	},
	{
		value: EOrderStatus.Preparing,
		text: '备餐中',
		status: 'processing',
	},
	{
		value: EOrderStatus.InDelivery,
		text: '配送中',
		status: 'processing',
	},
	{
		value: EOrderStatus.Completed,
		text: '已完成',
		status: 'success',
	},
	{
		value: EOrderStatus.Cancelled,
		text: '已取消',
		status: 'default',
	},
	{
		value: EOrderStatus.RefundInProgress,
		text: '退款中',
		status: 'error',
	},
	{
		value: EOrderStatus.Refunded,
		text: '已退款',
		status: 'success',
	},
	{
		value: EOrderStatus.RefundFailed,
		text: '退款失败',
		status: 'error',
	},
]);
