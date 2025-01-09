import { toValEnumMap } from '@/utils/enum';

/** 商品类别层级 (1: 一级, 2: 二级, 3: 三级, 4: 四级) */
export enum ECategoryLevel {
	Level1 = 1,
	Level2 = 2,
	Level3 = 3,
	Level4 = 4,
}

export const CategoryLevelMap = toValEnumMap<ECategoryLevel>([
	{
		value: ECategoryLevel.Level1,
		text: '一级',
	},
	{
		value: ECategoryLevel.Level2,
		text: '二级',
	},
	{
		value: ECategoryLevel.Level3,
		text: '三级',
	},
	{
		value: ECategoryLevel.Level4,
		text: '四级',
	},
]);
