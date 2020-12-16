import React from 'react';
import { render, fireEvent } from '@testing-library/react';
import UserOptions from "./index";
import '@testing-library/jest-dom/extend-expect';

describe('User Options', () => {
    const onCreateUser = jest.fn();
    const onAllUsers = jest.fn();
    const onManageUser = jest.fn();

    test('Should render all options', () => {
        const { getByText } = render(<UserOptions onCreateUser={onCreateUser} onAllUsers={onAllUsers} onManageUser={onManageUser} />);

        expect(getByText('Create User')).toBeInTheDocument();
        expect(getByText('Manage User')).toBeInTheDocument();
        expect(getByText('All Users')).toBeInTheDocument();
    });

    test('Clicking each button executes it\'s respective functionality', () => {
        const { getByText } = render(<UserOptions onCreateUser={onCreateUser} onAllUsers={onAllUsers} onManageUser={onManageUser} />);

        const createUserButton = getByText('Create User');
        const allUsersButton = getByText('All Users');
        const manageUserButton = getByText('Manage User');

        fireEvent.click(createUserButton);
        fireEvent.click(allUsersButton);
        fireEvent.click(manageUserButton);

        expect(onCreateUser).toBeCalledTimes(1);
        expect(onAllUsers).toBeCalledTimes(1);
        expect(onManageUser).toBeCalledTimes(1);
    });
});