import React from 'react';
import './styles.css';

interface UserProps {
    id: string;
    name: string;
}

const User: React.FC<UserProps> = ({id, name}) => {
    return (
        <div className="user-view-object" key={id}  data-testid="user-view">
            <p className="user-view-field user-view-id">ID: {id}</p>
            <p className="user-view-field user-view-name">Name: {name}</p>
        </div>
    );
};

export default User;