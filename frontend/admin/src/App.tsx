import React from 'react';
import { BrowserRouter } from 'react-router-dom';

import './style/index.less';
import MainLayout from '@/layout/MainLayout';

const App: React.FC = () => (
	<BrowserRouter>
		<MainLayout />
	</BrowserRouter>
);

export default App;
