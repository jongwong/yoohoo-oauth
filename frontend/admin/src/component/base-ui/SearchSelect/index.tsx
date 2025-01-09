import React, { useEffect, useMemo, useState } from 'react';
import type { SelectProps } from 'antd';
import { Select, Spin } from 'antd';
import { useGetState } from 'ahooks';

interface LazySelectProps extends Omit<SelectProps<any>, 'options'> {
	request: (params: {
		page: number;
		size: number;
		keyword?: string;
	}) => Promise<{ data: any[]; total: number }>;
	loadInitialOptions: () => Promise<any[]>;
	defaultQuery?: boolean; // 是否初始化时查询
	triggerLength?: number; // 输入多少字符后开始查询
}

const SearchSelect: React.FC<LazySelectProps> = props => {
	const {
		request,
		loadInitialOptions,
		defaultQuery = true,
		triggerLength = 2,
		fieldNames = { label: 'label', value: 'value', key: 'key' },
		value,
		onChange,
		...restProps
	} = props;
	const [options, setOptions] = useState<any[]>([]);
	const [loading, setLoading] = useState(false);
	const [keyword, setKeyword] = useState('');
	const [page, setPage, getPage] = useGetState(1);
	const [total, setTotal] = useState(0);

	const fetchData = async (reset = false) => {
		if (loading) return;

		setLoading(true);
		if (reset) {
			setPage(1);
		}
		try {
			const _page = getPage();
			const response = await request({ page: _page, size: 20, keyword });
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
			setKeyword(value);
			setPage(1);
			fetchData(true);
		}
	};

	const handleDropdownVisibleChange = (open: boolean) => {
		if (open && options.length === 0 && defaultQuery) {
			fetchData(true);
		}
	};

	// const fetchBackRequest = async (value: any) => {
	// 	if (backRequest && value) {
	// 		try {
	// 			const response = await backRequest(value);
	// 			setOptions([
	// 				{
	// 					[fieldNames.label]: response[fieldNames.label],
	// 					[fieldNames.value]: response[fieldNames.value],
	// 				},
	// 			]);
	// 		} catch (error) {
	// 			console.error('Back request error:', error);
	// 		}
	// 	}
	// };

	useEffect(() => {
		// if (value && backRequest) {
		// 	fetchBackRequest(value);
		// } else if (defaultQuery) {
		// 	fetchData(true);
		// }
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
			value={value}
			onChange={onChange}
			{...restProps}
		/>
	);
};

export default SearchSelect;
