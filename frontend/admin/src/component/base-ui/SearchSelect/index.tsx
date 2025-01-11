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
	loadInitialOptions?: (keys: string[]) => Promise<any[]>;
	triggerLength?: number; // 输入多少字符后开始查询
	initFetchType?: 'init' | 'open' | 'search';
	searchKeyword?: string;
}

const SearchSelect: React.FC<SearchSelectProps> = props => {
	const {
		request,
		labelInValue,
		onDropdownVisibleChange,
		loadInitialOptions,
		triggerLength = 2,
		fieldNames = { label: 'label', value: 'value', key: 'key' },
		value,
		initFetchType = 'search',
		searchKeyword = 'name',
		onChange,
		...restProps
	} = props;
	const [options, setOptions] = useState<any[]>([]);
	const [loading, setLoading] = useState(false);
	const [searchString, setSearchString, getSearchString] = useGetState('');
	const [page, setPage, getPage] = useGetState(1);
	const [total, setTotal] = useState(0);
	const initFetchRef = useRef(false);
	const [optionLoadInit, setOptionLoadInit, getOptionLoadInit] = useGetState(false);
	const fetchData = async (reset = false) => {
		if (loading) return;
		setLoading(true);
		if (reset) {
			setPage(1);
		}
		try {
			const _page = getPage();
			const response = await request({ page: _page, size: 20, [searchKeyword]: searchString });
			const { data, total } = response;
			if (total) {
				setOptions(prev => (reset ? data : [...prev, ...data]));
				setTotal(total);
				setPage(prev => prev + 1);
			}
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
		if (!initFetchRef.current && e && initFetchType === 'open') {
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

		if (!keys.length || getOptionLoadInit()) {
			return;
		}
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
		if (target.scrollTop + target.offsetHeight >= target.scrollHeight && options.length < total) {
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
		if (initFetchType === 'init') {
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
			options={formatOptions}
			value={!isNil(value) && formatOptions?.length ? value : undefined}
			onChange={onChange}
			labelInValue={labelInValue}
			{...restProps}
		/>
	);
};

export default SearchSelect;
