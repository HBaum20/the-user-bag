import React from 'react';
import { Button } from "@blueprintjs/core";
import './styles.css';

interface UserOptionsProps {
    onCreateUser: () => void;
    onAllUsers: () => void;
    onManageUser: () => void;
}

const UserOptions: React.FC<UserOptionsProps> = ({onCreateUser, onAllUsers, onManageUser}) => {
    return (
        <div id="user-options-group">
            <Button
                data-testid="app-create-user"
                className="user-options-button"
                onClick={onCreateUser}>Create User</Button>
            <Button
                data-testid="app-manage-user"
                className="user-options-button"
                onClick={onManageUser}>Manage User</Button>
            <Button
                data-testid="app-all-users"
                className="user-options-button"
                onClick={onAllUsers}>All Users</Button>
        </div>
    );
};

export default UserOptions;
