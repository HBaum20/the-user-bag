import React from 'react';
import './styles.css';

interface HeaderProps {
    title: string;
    subtitle: string;
}

const Header: React.FC<HeaderProps> = ({ title, subtitle}) => (
    <div id="header-body">
        <h1 className="header-text">{title}</h1>
        <hr className="header-text-divider" />
        <p className="header-text">{subtitle}</p>
    </div>
);

export default Header;