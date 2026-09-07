import axiosClient from '../api/axiosClient';

const userService = {
  getProfile: () => {
    return axiosClient.get('/users/me');
  },

  changePassword: (passwordData) => {
    return axiosClient.put('/users/me/password', passwordData);
  },
};

export default userService;
