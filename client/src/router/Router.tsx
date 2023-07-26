import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { MainFeed } from '../pages/FeedPage/MainFeed';
import { PerfumeFeed } from '../pages/FeedPage/PerfumeFeed';
import SearchTabPage from '../pages/SearchPage/SearchTabPage';
import SearchMyPerfume from '../pages/SearchPage/SearchMyPerfume';
import PerfumeDetail from '../pages/PerfumePage/PerfumeDetail';
import PostDetail from '../pages/PostDetailPage/PostDetail';
import { MyDrawerPage } from '../pages/DrawerPage/MyDrawerPage';
import FollowList from '../pages/MyPage/FollowList';
import Post from '../pages/PostPages/Post';
import ProfileUpdate from '../pages/MyPage/ProfileUpdate';

export default function Router() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/main-feed" element={<MainFeed />}></Route>
        <Route path="/perfume-feed" element={<PerfumeFeed />}></Route>
        <Route path="/search" element={<SearchTabPage />}></Route>
        <Route path="/search-myperfume" element={<SearchMyPerfume />}></Route>
        <Route path="/post" element={<Post />}></Route>
        <Route path="/post-detail" element={<PostDetail />}></Route>
        <Route path="/perfume-detail" element={<PerfumeDetail />}></Route>
        <Route path="/my-drawer" element={<MyDrawerPage />}></Route>
        <Route path="/follow-list" element={<FollowList />}></Route>
        <Route path="/profile-update" element={<ProfileUpdate />}></Route>
      </Routes>
    </BrowserRouter>
  );
}
