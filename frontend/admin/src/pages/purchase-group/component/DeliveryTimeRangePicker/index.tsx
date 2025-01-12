import React, { useEffect, useState } from 'react';
import { DatePicker, Flex, Space } from 'antd';
import dayjs, { Dayjs } from 'dayjs';

import styles from './index.module.less';
import classNames from 'classnames';

type TimeRange = {
	from: Dayjs;
	to: Dayjs;
};

type DeliveryTimeRangePickerProps = {
	generateTimeRanges: () => TimeRange[]; // 生成时间范围的函数
	config?: any; // DatePicker 的额外配置
	value?: [Dayjs, Dayjs] | null; // 当前选择的值
	onChange?: (val: [Dayjs | undefined, Dayjs | undefined]) => void; // 改变事件回调，返回起始和结束时间
};

const DeliveryTimeRangePicker: React.FC<DeliveryTimeRangePickerProps> = ({
	generateTimeRanges,
	config = {},
	value,
	onChange,
	...rest
}) => {
	const [currentValue, setCurrentValue] = useState<any>();
	const range = generateTimeRanges();
	const [open, setOpen] = useState(false);
	useEffect(() => {
		setCurrentValue(value);
	}, [value]);

	const getActive = () => {
		return range.find((item, index) => {
			const str = formatItemContent(item.from, item.to);
			const c = formatItemContent(currentValue?.[0], currentValue?.[1]);
			return str === c;
		});
	};
	const handleChange = (e: Dayjs | null, change: boolean, rangeItem?: any) => {
		const range = generateTimeRanges();
		const firstRange = rangeItem || getActive();

		// 提取时间偏移
		const fromTimeOffset = firstRange.from.diff(firstRange.from.startOf('day'), 'millisecond');
		const toTimeOffset = firstRange.to.diff(firstRange.to.startOf('day'), 'millisecond');

		let fromTimestamp = 0;
		let toTimestamp = 0;

		// 获取结束时间
		const startVal = e;

		// 获取结束时间
		let endVal = currentValue ? currentValue[1] : null;

		endVal = startVal;

		// 根据用户选择的日期生成时间戳
		fromTimestamp = startVal?.startOf('day').valueOf() + fromTimeOffset;
		toTimestamp = endVal?.startOf('day').valueOf() + toTimeOffset;

		const _val: any = e ? [dayjs(fromTimestamp), dayjs(toTimestamp)] : undefined;
		setCurrentValue(_val);

		// 触发外部的 onChange 回调，返回时间范围
		if (change) {
			onChange?.(_val);
			setOpen(false);
		}
	};

	const formatItemContent = (from?: Dayjs, to?: Dayjs) => {
		if (from && to) {
			return from.format('HH:mm') + ' - ' + to.format('HH:mm');
		}
		return '';
	};

	return (
		<DatePicker
			{...config}
			{...rest}
			value={currentValue ? currentValue?.[0] : undefined} // DatePicker 显示值为开始时间
			panelRender={panelNode => {
				return (
					<div>
						<div style={{ display: 'flex' }}>
							<div>{panelNode}</div>
							<div
								className={'h-1-1'}
								style={{
									padding: '32px 8px',
								}}>
								{range.map((item, index) => {
									const str = formatItemContent(item.from, item.to);
									const c = formatItemContent(currentValue?.[0], currentValue?.[1]);
									const isActive = str === c;
									return (
										<div
											key={index}
											className={classNames(
												styles['time-picker-item'],
												isActive && styles['time-picker-item-active']
											)}
											onClick={() => {
												// 设置开始时间和结束时间
												handleChange(currentValue?.[0] || dayjs(), true, item);
											}}>
											{str}
										</div>
									);
								})}
							</div>
						</div>
						<Flex justify={'end'} className={'w-1-'} style={{ padding: '8px' }}>
							<Space>
								<a
									onClick={() => {
										setCurrentValue(value);
										setOpen(false);
									}}>
									取消
								</a>
								<a
									onClick={() => {
										if (getActive()) {
											setOpen(false);
											onChange?.(currentValue);
										}
									}}>
									确认
								</a>
							</Space>
						</Flex>
					</div>
				);
			}}
			showNow={false}
			onOpenChange={e => {
				if (e) {
					setOpen(e);
				}
			}}
			format={e => {
				if (value?.[0] && value?.[1] && e) {
					const formtStr = e.format('YYYY') === dayjs().format('YYYY') ? 'MM-DD' : 'YYYY-MM-DD';
					return (
						e.format(formtStr) +
						' ' +
						dayjs(value?.[0]).format('HH:mm') +
						' ~ ' +
						e.format(formtStr) +
						' ' +
						dayjs(value?.[1]).format('HH:mm')
					);
				}
				return '';
			}}
			open={open}
			onChange={date => {
				handleChange(date, false);
			}}
		/>
	);
};

export default DeliveryTimeRangePicker;
