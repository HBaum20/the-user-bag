import React, {ChangeEvent} from 'react';
import {UserModel} from "../../../../../models";
import {Button, FormGroup, InputGroup} from "@blueprintjs/core";

interface ManageUserProps {
    user: UserModel;
    onBack: () => void;
}

const ManageUser: React.FC<ManageUserProps> = ({user, onBack}) => {
    return (
        <div>
            <FormGroup>
                <label htmlFor="id">ID </label>
                <InputGroup
                    id="id"
                    name="id"
                    type="text"
                    value={user.id}
                    large
                    disabled
                />
                <label htmlFor="name">Name </label>
                <InputGroup
                    id="name"
                    name="name"
                    type="text"
                    value={user.name}
                    large
                    disabled
                />
                <label htmlFor="deposit">Balance </label>
                <InputGroup
                    id="deposit"
                    name="deposit"
                    type="number"
                    large
                    min={0}
                    value={user.balance.toString()}
                    disabled
                />
                <label htmlFor="email">Email Address </label>
                <InputGroup
                    id="email"
                    name="email"
                    type="text"
                    large
                    value={user.email}
                    disabled
                />
                <label htmlFor="phone">Phone Number </label>
                <InputGroup
                    id="phone"
                    name="phone"
                    type="text"
                    large
                    value={user.phone}
                    disabled
                />
                <label htmlFor="password">Hashed Password </label>
                <InputGroup
                    id="password"
                    name="password"
                    type="password"
                    large
                    value={user.password}
                    disabled
                />
            </FormGroup>
            <Button large onClick={onBack} data-testid="manage-user-back-button">Back</Button>
        </div>
    );
};

export default ManageUser;