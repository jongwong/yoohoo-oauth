export const transformToFields = (val: Record<string, any> = {}) => {
	//转成name，value的数组
	return Object.keys(val).map(key => {
		return {
			name: key,
			value: val[key],
		};
	});
};
