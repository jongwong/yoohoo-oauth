/// <reference types="vite-plugin-svgr" />

declare module '*.svg' {
	import React from 'react';
	const ReactComponent: React.FC<React.SVGProps<SVGSVGElement>>;
	export default ReactComponent;
}

declare module '*.module.less' {
	const classes: { [key: string]: string };
	export default classes;
}
