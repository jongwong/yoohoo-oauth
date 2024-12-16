import React from 'react';
import {BrowserRouter} from 'react-router-dom';

import './style/normalize.css';
import MainLayout from "@/layout/MainLayout";

const App: React.FC = () => <BrowserRouter><MainLayout/></BrowserRouter>;

export default App;
