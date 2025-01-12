import React, { useEffect, useMemo, useRef, useState } from 'react';

import { useGetState } from 'ahooks';
import type { SelectProps } from 'antd';
import { Select, Spin } from 'antd';
import { isNil } from 'lodash';

export interface SearchSelectProps extends Omit<SelectProps<any>, 'options'> {
	request: (
		params: {
			page: number;
			size: number;
		} & Record<string, any>
	) => Promise<{ data: any[]; total: number }>;
	params?: Record<string, any>;
	loadInitialOptions?: (keys: string[]) => Promise<any[]>;
	triggerLength?: number; // 输入多少字符后开始查询
	triggerMode?: 'init' | 'open' | 'search';
	searchKeyword?: string;
}

const DEFAULT_PAGE_SIZE = 10;

const SearchSelect: React.FC<SearchSelectProps> = props => {
	const {
		params,
		request,
		labelInValue,
		onDropdownVisibleChange,
		loadInitialOptions,
		triggerLength = 2,
		fieldNames = { label: 'label', value: 'value', key: 'key' },
		value,
		triggerMode = 'search',
		searchKeyword = 'name',
		onChange,
		...restProps
	} = props;
	const [options, setOptions, getOptions] = useGetState<any[]>([]);
	const [loading, setLoading] = useState(false);
	const [searchString, setSearchString, getSearchString] = useGetState('');
	const [page, setPage, getPage] = useGetState(1);
	const [total, setTotal] = useState(-1);
	const initFetchRef = useRef(false);
	const [optionLoadInit, setOptionLoadInit, getOptionLoadInit] = useGetState(false);
	const fetchData = async (reset = false) => {
		if (loading) return;

		let _page = getPage();
		if (reset) {
			_page = 1;
		} else {
			_page = getPage() + 1;
		}
		if (_page * DEFAULT_PAGE_SIZE >= total && total >= 0) {
			return;
		}

		setLoading(true);
		try {
			const _page = getPage();
			const response = await request({
				...params,
				page: _page,
				size: DEFAULT_PAGE_SIZE,
				[searchKeyword]: searchString,
			});
			const { data, total } = response;
			if (total) {
				setOptions(prev => (reset ? data : [...prev, ...data]));

				data?.forEach(it => {
					const _name = fieldNames.value;
					const _key = it[_name!];
					openHasInitMapRef.current[_key] = true;
				});
				setPage(_page);
			}
			setTotal(total || 0);
		} finally {
			setLoading(false);
		}
	};

	const handleSearch = (value: string) => {
		if (value.length >= triggerLength) {
			setSearchString(value);
			setPage(1);
			fetchData(true);
		}
	};

	const handleDropdownVisibleChange = (e: boolean) => {
		setOptionLoadInit(true);

		if (!initFetchRef.current && e && triggerMode === 'open') {
			fetchData(true);
		}

		onDropdownVisibleChange?.(e);
	};

	const openHasInitMapRef = useRef<Record<string, any>>({});

	useEffect(() => {
		if (options.length) {
			return;
		}
		const keys: string[] = [];
		const val = !Array.isArray(value) && !isNil(value) ? [value] : value;
		if (!labelInValue) {
			val?.forEach(e => {
				if (!openHasInitMapRef.current[e]) {
					keys.push(e);
				}
			});
		}
		if (!keys.length) {
			return;
		}
		setOptionLoadInit(true);
		loadInitialOptions?.(keys).then(e => {
			e.forEach(it => {
				if (!openHasInitMapRef.current[e]) {
					const _name = fieldNames.value;
					const _key = it[_name!];
					openHasInitMapRef.current[_key] = true;

					setOptions(old => {
						return [it, ...old];
					});
				}
			});
		});
	}, [value]);

	const handleScroll = (event: React.UIEvent<HTMLDivElement, UIEvent>) => {
		const target = event.target as HTMLElement;
		const threshold = 50; // 距离底部的像素值
		if (target.scrollTop + target.offsetHeight >= target.scrollHeight - threshold) {
			fetchData();
		}
	};

	const formatOptions = useMemo(() => {
		const labelKey = fieldNames?.label;
		const valueKey = fieldNames?.value;
		const keyName = (fieldNames as any)?.key;

		return options.map(item => ({
			...item,
			label: item[labelKey!],
			value: item[valueKey!],
			key: item[keyName],
		}));
	}, [options]);
	useEffect(() => {
		if (triggerMode === 'init') {
			fetchData(true);
		}
	}, []);
	return (
		<Select
			showSearch
			filterOption={false}
			onSearch={handleSearch}
			onDropdownVisibleChange={handleDropdownVisibleChange}
			dropdownRender={menu => (
				<div onScroll={handleScroll}>
					{menu}
					{loading && (
						<div style={{ textAlign: 'center', padding: 8 }}>
							<Spin size="small" />
						</div>
					)}
				</div>
			)}
			notFoundContent={loading ? '请求中...' : '暂无数据'}
			options={formatOptions}
			value={!isNil(value) && formatOptions?.length ? value : undefined}
			onChange={onChange}
			labelInValue={labelInValue}
			{...restProps}
		/>
	);
};

export default SearchSelect;
